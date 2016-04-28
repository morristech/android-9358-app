import React, {
  Image,
  ListView,
  StyleSheet,
  Text,
  View,
  ScrollView,
  TouchableOpacity,
  Picker,
  Platform,
  BackAndroid,
} from 'react-native';

import * as $$ from './Constant';
import {ToastAndroid, SharedPreference} from './NativeModuleExport';

export default class PaidCopuonUserDetail extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            details: new ListView.DataSource({
              rowHasChanged: (row1, row2) => row1 !== row2,
            }),
            loaded: false,
            records: null,
            serverHost:null,
            userToken:null,
            actId: null,
            couponStatus:'0',
            pageSize:20,
            pageStart:1,
            pageCount:0,
        };
    }

    componentDidMount() {
        this.setState({
            actId: this.props.actId,
        });
        SharedPreference.getConstants((serverHost, userToken) => {
            this.setState({
                userToken: userToken,
                serverHost: serverHost,
            })
            this.fetchData();
        })
    }

    _onPressBack = () => {
        const { navigator } = this.props;
        if (navigator) {
            navigator.pop();
        }
        return true;
    };

    fetchData() {
        fetch(this.state.serverHost + $$.urlPaidCouponUserDetail, {
                method:'POST',
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded"
                },
                body: "actId="+this.state.actId
                     +"&sessionType=app"
                     +"&pageSize="+this.state.pageSize
                     +"&pageNumber="+this.state.pageStart
                     +"&couponStatus="+this.state.couponStatus
                     +"&token="+this.state.userToken,
             })
            .then((response) => response.json())
            .then((response) => {
                var records = this.state.records;
                if (records != null) {
                    for (let i = 0; i < response.respData.length; i++) {
                        records.push(response.respData[i]);
                    }
                } else {
                    records = response.respData;
                }
                this.setState({
                    records: records,
                    details: this.state.details.cloneWithRows(records),
                    loaded: true,
                    pageCount: response.pageCount,
                })
            })
            .done();
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
                        <Image source={require('./img/left_icon.png')} style={styles.titleBacK}/>
                    </TouchableOpacity>
                    <Text style={styles.title}>购买详情</Text>
                </View>
            </View>
        );
    }

    renderContentView() {
        if(!this.state.loaded) {
            return this.renderLoadingView();
        }

        return (
            <View style={styles.container}>
                <View style={styles.titleSection}>
                    <TouchableOpacity onPress={this._onPressBack.bind(this)}>
                        <Image source={require('./img/left_icon.png')} style={styles.titleBacK}/>
                    </TouchableOpacity>
                    <TouchableOpacity onPress={this._onPressBack.bind(this)}>
                        <Text style={styles.title}>购买详情</Text>
                    </TouchableOpacity>
                </View>
                <View style={styles.header}>
                    <View style={styles.headerItemWithRightBorder}>
                        <Text>用户</Text>
                    </View>
                    <View style={styles.headerItemWithRightBorder}>
                    <Picker style={styles.picker}
                        mode='dropdown'
                        selectedValue={this.state.couponStatus}
                        onValueChange={(val) => {
                            this.setState({
                                couponStatus: val,
                                pageStart:1,
                                records:null,});
                            this.fetchData();
                            }
                        }>
                        <Picker.Item label="全部状态" value='0'/>
                        <Picker.Item label="未使用" value='1' />
                        <Picker.Item label="已使用" value='2' />
                        <Picker.Item label="已过期" value='3' />
                    </Picker>
                    </View>
                    <View style={styles.headerItem}>
                        <Text>购买时间</Text>
                    </View>
                </View>
                <ListView
                    dataSource={this.state.details}
                    renderRow={this.renderDetail.bind(this)}
                    style={styles.listView}
                    onEndReached={this.loadMore.bind(this)}
                    >
                </ListView>
            </View>
        );
    }

    renderDetail(detail, sectionId, rowId) {
        return (
            <View style={styles.row}>
                <View style={styles.rowUser}>
                    <Image source={{uri: detail.headImgUrl}} style={styles.avatar}/>
                    <View style={{flexDirection:'column'}}>
                        <View>
                            <Text>{detail.userName}</Text>
                        </View>
                        <View>
                            <Text>{detail.telephone}</Text>
                        </View>
                    </View>
                </View>
                <View style={styles.rowStatus}>
                    <Text>{detail.couponStatusDescription}</Text>
                </View>
                <View style={styles.rowDate}>
                    <Text>{detail.getDate.split(' ')[0]}</Text>
                </View>
            </View>
        );
    }

    loadMore() {
        var pageStart = this.state.pageStart + 1;
        if (pageStart <= this.state.pageCount) {
            this.setState({
                pageStart: pageStart,
            });
            this.fetchData();
        }
    }
}

const styles = StyleSheet.create({
    container : {
        backgroundColor: '#fff7e3',
        flex:1,
    },

    titleSection : {
        flexDirection:'row',
        padding:12,
        backgroundColor:'#ff6666',
        alignItems:'center',
    },

    title: {
        fontSize:20,
        color:'#ffffff',
    },

    header: {
        height:36,
        flexDirection:'row',
        alignItems:'center',
        borderBottomColor:'#d9d9d9',
        borderBottomWidth:1,
    },

    headerItem: {
        justifyContent:'center',
        alignItems:'center',
        flex:1,
    },

    picker:{
        height:24,
        flex:1,
    },

    headerItemWithRightBorder: {
        justifyContent:'center',
        alignItems:'center',
        flexDirection:'row',
        flex:1,
        borderRightColor:'#d9d9d9',
        borderRightWidth:1,
    },

    listView: {
        flex:1,
        backgroundColor: '#fff7e3',
    },

    row: {
        backgroundColor:'#fff',
        flexDirection: 'row',
        borderBottomWidth: 1,
        borderBottomColor: '#d9d9d9',
        padding:8,
        justifyContent:'center',
        alignItems:'center',
    },

    avatar: {
        width:36,
        height:36,
        borderRadius:18,
    },

    rowUser: {
        flexDirection:'row',
        flex:1,
    },

    rowStatus: {
        flex:1,
        alignItems:'center',
        justifyContent:'center'
    },

    rowDate: {
        flex:1,
        alignItems:'center',
    },

    titleBacK : {
        alignSelf:'flex-start',
        width:15,
        height:15,
    },

});
