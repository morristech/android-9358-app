import React, {
  Image,
  ListView,
  StyleSheet,
  Text,
  View,
  TouchableOpacity,
  Platform,
  BackAndroid,
} from 'react-native';

import * as $$ from './Constant';
import {ToastAndroid, SharedPreference} from './NativeModuleExport';
import CouponDetail from './CouponDetail';

export default class MarketingFragment extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
          couponList: new ListView.DataSource({
              rowHasChanged: (row1, row2) => row1 !== row2,
          }),
          loaded: false,
        };
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
        return false;
    };

    componentDidMount() {
        this.fetchData();
    }

    fetchData() {
         SharedPreference.getConstants((serverHost, userToken) => {
            fetch(serverHost + $$.urlCouponList, {
                    method:'POST',
                    headers: {
                        "Content-Type": "application/x-www-form-urlencoded"
                    },
                    body: "sessionType=app&token="+userToken,
                 })
                 .then((response) => response.json())
                 .then((response) => {
                      this.setState({
                            couponList: this.state.couponList.cloneWithRows(response.respData.coupons),
                            loaded: true,
                       });
                 })
                 .done();
         });
     }

    render() {
        return this.renderContentView();
    }

    renderContentView() {

        if(!this.state.loaded) {
             return this.renderLoadingView();
        }

        return (
           <View style={styles.container}>
                <View style={styles.titleSection}>
                    <Text style={styles.title}>分享有礼</Text>
                </View>
                <ListView
                    dataSource={this.state.couponList}
                    renderRow={this.renderCoupon.bind(this)}
                    style={styles.listView}>
                </ListView>
           </View>
        );
    }

    // Rendering the Loading View
    renderLoadingView() {
        return (
            <View style={styles.container}>
                <View style={styles.titleSection}>
                    <Text style={styles.title}>分享有礼</Text>
                </View>
                <Text>
                    Loading Coupons, please wait...
                </Text>
            </View>
        );
    }

    _pressButton(actId) {
        const { navigator } = this.props;
        if (navigator) {
            navigator.push({
                name:'CouponDetail',
                component:CouponDetail,
                params: {
                    actId: actId,
                }
            });
        }
    }

    renderCoupon(coupon, sectionId, rowId) {
        return (
            <TouchableOpacity onPress={() => this._pressButton(coupon.actId)}>
                <View style={styles.row}>
                  <View style={styles.itemTitleSection}>
                    <View style={styles.itemTitleBox}>
                        <Text style={styles.itemTitle}>{coupon.actTitle}</Text>
                    </View>
                    <View style={styles.itemContentBox}>
                        <Text style={styles.itemContent}>{coupon.consumeMoneyDescription}</Text>
                    </View>
                  </View>
                  <View style={styles.itemShareSection}>
                    <Text style={styles.itemCouponPeriod}>{coupon.couponPeriod}</Text>
                    <View style={styles.itemShareButton}>
                        <Text style={styles.itemShare}>分享</Text>
                    </View>
                  </View>
                </View>
            </TouchableOpacity>
        );
    }

}

const styles = StyleSheet.create({

    container : {
        flexDirection: 'column',
        backgroundColor: '#fff7e3',
        flex:1,
    },

    titleSection : {
        padding:12,
        backgroundColor:'#ff6666',
        justifyContent :'center',
    },

    title: {
    fontSize:20,
    color:'#ffffff'
    },

    listView: {
        flex:1,
        backgroundColor: '#fff7e3',
    },

    row: {
        margin:8,
        flex: 1,
        flexDirection: 'column',
        borderWidth: 1,
        borderColor: '#d9d9d9',
    },

    itemTitleSection: {
        padding:12,
        flex: 1,
        flexDirection: 'column',
        backgroundColor: '#f24f52',
        margin:1,
        borderTopLeftRadius:12,
        borderTopRightRadius:12,
    },

    itemTitleBox: {
        marginBottom: 8,
    },

    itemTitle : {
        color:'#ffffff',
        fontSize: 20,
        textAlign: 'left',
    },

    itemContentBox : {
        backgroundColor:'#f68486',
        marginBottom: 8,
        paddingLeft:8,
    },

    itemContent: {
        fontSize: 14,
        color:'#ffffff',
    },

    itemShareSection: {
        margin:1,
        padding:8,
        flex: 1,
        backgroundColor: '#ffffff',
        flexDirection: 'row',
        borderBottomLeftRadius:5,
        borderBottomRightRadius:5,
        justifyContent:'space-between',
    },

    itemCouponPeriod: {
        fontSize: 14,
        textAlign: 'left',
    },

    itemShareButton: {
        borderRadius:5,
        borderWidth: 1,
        paddingLeft:5,
        paddingRight:5,
        borderColor: '#f24f52',
    },

    itemShare : {
        color:'#f24f52',
    },
});