import React, {
    View,
    Text,
    Image,
    ScrollView,
    StyleSheet,
    TouchableOpacity,
    WebView,
    Platform,
    BackAndroid,
} from 'react-native';

import * as $$ from './Constant';
import {AlertDialog, ToastAndroid, SharedPreference, Share} from './NativeModuleExport';
import PaidCouponUserDetail from './PaidCouponUserDetail';

export default class CouponDetail extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            serverHost:null,
            userToken:null,
            loaded: false,
            actId: null,
            respData: null,
            activities:null,
        };
    }

    componentDidMount() {
        this.setState({
            actId: this.props.actId,
        })
        this.fetchData();
    }

    componentWillMount() {
        if (Platform.OS === 'android') {
            BackAndroid.addEventListener('hardwareBackPress', this._onPressBack);
        }
    }

    componentWillUnmount() {
        if (Platform.OS === 'android') {
            BackAndroid.removeEventListener('hardwareBackPress', this._onPressBack);
        }
    }

    _onPressBack = () => {
        const { navigator } = this.props;
        if (navigator) {
            navigator.pop();
        }
        return true;
    };

    /*****************************  Biz Log ********************************/

    fetchData() {
        SharedPreference.getConstants((serverHost, userToken) => {
        this.setState({serverHost:serverHost});
        fetch(serverHost + $$.urlCouponInfo, {
                method:'POST',
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded"
                },
                body: "actId=" + this.state.actId + "&sessionType=app&token=" + userToken,
             })
             .then((response) => response.json())
             .then((response) => {
                if (response.statusCode == '200') {
                    this.setState({
                        userToken:userToken,
                        respData: response.respData,
                        activities: response.respData.activities,
                        loaded: true,
                    });
                } else {
                    ToastAndroid.show(response.msg, ToastAndroid.SHORT);
                    this.pressBack();
                }
               })
             .done();
    });
     }


    render() {
        return this.renderContentView();
    }

    // Rendering the Loading View
    renderLoadingView() {
        return (
            <View style={styles.container}>
                <View style={styles.titleSection}>
                    <TouchableOpacity onPress={this._onPressBack}>
                        <Text style={styles.title}>分享有礼</Text>
                    </TouchableOpacity>
                </View>
                <Text>
                    Loading Coupons, please wait...
                </Text>
            </View>
        );
    }

    share() {
        Share.share(this.state.respData.clubName + "-" + this.state.activities.actTitle,
            this.state.activities.consumeMoneyDescription+"，超值优惠，超值享受。快来约我。",
            this.state.respData.shareUrl,
            this.state.respData.imageUrl
        );
    }

    renderContentView() {
        if(!this.state.loaded) {
            return this.renderLoadingView();
        }

        if (this.state.activities.couponType == 'paid') {
            return this.renderPaidCopuonView();
        } else if (this.state.activities.couponType == 'redpack') {
            return this.renderRedpackCouponView();
        }
    }

    //分享有礼券
    renderRedpackCouponView() {
        /*************************************定义常用变量*************************************/
        var serviceItem='';

        for(var i= 0,l=this.state.respData.items.length;i<l;i++){
            serviceItem+=','+this.state.respData.items[i].name;
        }

        if(serviceItem!='') {
            serviceItem=serviceItem.substring(1);
        }

        if(serviceItem.length==0) {
            serviceItem="使用不限";
        }

        if(!this.state.activities.time || this.state.activities.time.length==0) {
            this.state.activities.time="使用不限";
        }

        if(!this.state.activities.actContent || this.state.activities.actContent.length==0) {
            this.state.activities.actContent="无";
        }

        return (
            <ScrollView contentContainerStyle={styles.container}>
                <View>
                     <View style={styles.titleSection}>
                        <TouchableOpacity onPress={this._onPressBack}>
                            <Image source={require('./img/left_icon.png')} style={styles.titleBacK}/>
                        </TouchableOpacity>
                        <TouchableOpacity onPress={this._onPressBack}>
                            <Text style={styles.title}>分享有礼</Text>
                        </TouchableOpacity>
                     </View>

                    <View style={{backgroundColor:'#fff', padding:8, marginLeft:12, marginTop:12, marginRight:12}}>
                        <View style={{alignItems:'center'}}>
                            <Text style={{ fontSize:20, color:'#f53', alignItems:'center'}}>分享领红包</Text>
                        </View>
                        <Text style={{fontSize:16, color:'#666'}}>
                            分享优惠给您的客人，一旦这些客人使用了这个优惠，您也将获得{this.state.activities.sysCommission}元现金红包，您的客人以及客人的朋友使用这个优惠后，您也将获得{this.state.activities.sysCommission}元红包，红包会自动存入您的账户。
                        </Text>
                    </View>

                    <View style={styles.button}>
                        <TouchableOpacity onPress={this.share.bind(this)}>
                            <Text style={styles.buttonText}>立即分享</Text>
                        </TouchableOpacity>
                    </View>

                    <View style={{alignItems:'center',marginTop:12,}}>
                        <View style={{flexDirection:'row',justifyContent:'flex-start'}}>
                            <Text style={{fontSize:18, color:'#FF6565'}}>优惠金额</Text>
                            <Text style={{fontSize:24, color:'#FF6565'}}>{this.state.activities.actValue}</Text>
                            <Text style={{fontSize:18, color:'#FF6565'}}>元</Text>
                        </View>
                    </View>

                    <View style={styles.qrcodeSectionForRedpack} >
                        <Image source={{uri: this.state.serverHost + $$.urlUserQrCode + "?token="+this.state.userToken+"&actId="+this.state.actId+"&sessionType=app"}}
                            style={styles.qrCode} />
                         <View>
                            <Text style={styles.actTitleText}>用户扫描二维码即得优惠！</Text>
                        </View>
                    </View>

                    <View style={styles.itemDescriptionOuterView}>
                        <View style={styles.itemDescriptionTitleView} >
                            <Text style={styles.itemDescriptionTitleText} >项目限定</Text>
                        </View>
                        <Text style={styles.itemDescriptionContentText} >
                            {serviceItem}
                        </Text>
                    </View>

                    <View style={styles.itemDescriptionOuterView}>
                        <View style={styles.itemDescriptionTitleView} >
                            <Text style={styles.itemDescriptionTitleText} >使用时段</Text>
                        </View>
                        <Text style={styles.itemDescriptionContentText} >
                            {this.state.activities.time}
                        </Text>
                    </View>

                    <View style={styles.itemDescriptionOuterView}>
                        <View style={styles.itemDescriptionTitleView} >
                            <Text style={styles.itemDescriptionTitleText} >使用规则</Text>
                        </View>
                        <View style={{flex:1}}>
                            <WebView style={{backgroundColor:'#00000000',height:200}}
                                source={{html:this.state.activities.actContent}}
                                javaScriptEnabled={true}
                                />
                        </View>
                    </View>
                </View>
            </ScrollView>
        );
    }

    _pressPaidCouponUserDetailButton(actId) {
        const { navigator } = this.props;
        if (navigator) {
            navigator.push({
                name:'PaidCouponUserDetail',
                component:PaidCouponUserDetail,
                params: {
                    actId: actId,
                }
            });
        }
    }

     // 点钟券
     renderPaidCopuonView() {
        return (
            <ScrollView contentContainerStyle={styles.container}>
                    <View style={styles.titleSection}>
                        <TouchableOpacity onPress={this._onPressBack}>
                            <Image source={require('./img/left_icon.png')} style={styles.titleBacK}/>
                        </TouchableOpacity>
                        <TouchableOpacity onPress={this._onPressBack}>
                            <Text style={styles.title}>点钟券</Text>
                        </TouchableOpacity>
                    </View>
                    <View style={styles.qrcodeSection} >
                        <Image source={{uri: this.state.serverHost + $$.urlUserQrCode + "?token="+this.state.userToken+"&actId="+this.state.actId+"&sessionType=app"}}
                            style={styles.qrCode} />
                         <View>
                            <Text style={styles.actTitleText}>{this.state.activities.consumeMoneyDescription}</Text>
                        </View>
                    </View>
                    <View style={styles.commentSection}>
                        <View style={styles.couponPeriodView}>
                            <Image source={require('./img/call.png')} style={styles.icon}/>
                            <Text>有效时间: {this.state.activities.couponPeriod}</Text>
                        </View>
                        <View style={styles.actContentView}>
                            <View style={{flexDirection:'row',}}>
                                <Image source={require('./img/desc.png')} style={styles.icon} />
                                <Text>活动说明</Text>
                            </View>
                            <View style={{flex:1}}>
                                <WebView style={{backgroundColor:'#00000000',height:32, marginLeft:12,}}
                                    javaScriptEnabled={true}
                                    source={{html:this.state.activities.actContent}}
                                    automaticallyAdjustContentInsets={false}/>
                            </View>
                        </View>
                    </View>
                    <View style={styles.button}>
                        <TouchableOpacity onPress={() => this._pressPaidCouponUserDetailButton(this.state.actId)}>
                            <Text style={styles.buttonText}>购买详情</Text>
                        </TouchableOpacity>
                     </View>
                     <View style={styles.button}>
                         <TouchableOpacity onPress={this.share.bind(this)}>
                             <Text style={styles.buttonText}>分享</Text>
                         </TouchableOpacity>
                     </View>
                     <View style={{flexDirection:'column',marginLeft:12, marginRight:12, marginTop:12}}>
                        <Text>注：</Text>
                        <Text>客人的点钟券过期后，您将获得{this.state.activities.baseCommission}元打赏。</Text>
                        <Text>客户的点钟券使用后，您将获得{this.state.activities.commission}元打赏。</Text>
                     </View>
            </ScrollView>
        );
     }

}

const styles = StyleSheet.create({

    container : {
        flexDirection: 'column',
        backgroundColor: '#fff7e3',
    },

    titleSection : {
        padding:12,
        backgroundColor:'#ff6666',
        alignItems:'center',
        flexDirection:'row',
    },

    titleBacK : {
        alignSelf:'flex-start',
        width:15,
        height:15,
    },

    title: {
        fontSize:20,
        color:'#ffffff'
    },

    qrcodeSection: {
        flexDirection:'column',
        flex:1,
        backgroundColor:'#fff',
        alignItems:'center',
    },

    qrcodeSectionForRedpack: {
        flexDirection:'column',
        flex:1,
        backgroundColor:'#fff',
        alignItems:'center',
        marginLeft:12,
        marginRight:12,
    },

    qrCode: {
        width:180,
        height:180,
    },

    actTitleText: {
        fontSize:18,
        marginBottom:12,
    },

    commentSection: {
        marginTop:12,
        backgroundColor:'#fff',
        flexDirection:'column',
    },

    couponPeriodView: {
        flexDirection:'row',
        borderBottomWidth:1,
        borderBottomColor:'#d9d9d9',
        padding:12,
    },

    actContentView: {
        flexDirection:'column',
        padding:12,
    },

    icon: {
        width:15,
        height:15
    },

    button : {
        marginTop:12,
        marginLeft:12,
        marginRight:12,
        borderRadius:2,
        backgroundColor:'#fb5549',
        height:40,
        justifyContent:'center',
    },

    buttonText: {
        color:'#fff',
        textAlign:'center',
        fontSize:18,
    },

    itemDescriptionOuterView: {
        marginTop:12,
        marginLeft:12,
        marginRight:12,
    },

    itemDescriptionTitleView : {
        alignItems:'center',
    },

    itemDescriptionTitleText : {
        fontSize:18,
        color:'#333',
    },
});