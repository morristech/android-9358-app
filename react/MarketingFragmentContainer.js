import React, {
    Navigator,
    Platform,
    BackAndroid,
} from 'react-native';
import MarketingFragment from './MarketingFragment';

export default class MarketingFragmentContainer extends React.Component {

    render() {
        let defaultName = 'MarketingFragment';
        let defaultComponent = MarketingFragment;

        return (
            <Navigator
                initialRoute = {{ name: defaultName, component: defaultComponent}}
                configureScene = {
                    (route) => { return Navigator.SceneConfigs.PushFromRight;}
                }

                renderScene = {
                    (route, navigator) => {
                        let Component = route.component;
                        return <Component {...route.params} navigator={navigator} />
                    }
                }
            />
        );
    }

}