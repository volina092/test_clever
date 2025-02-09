require: scripts.js
require: userData.sc

require: patterns.sc
    module = sys.zb-common
require: dateTime/dateTime.sc
    module = sys.zb-common

init:
    $global.$ = {
        __noSuchProperty__: function(property) {
            return $jsapi.context()[property];
        }
    };

theme: /

    state: Start
        q!: start
        script:
            $session.data = $request.data;
            $session.data.channel = parseChannel($.request.data.channel);
        # чтобы, если вдруг данные не пришли, не выпадала ошибка
        if: $session.data && $session.data.client && $session.data.client.age && $session.data.client.age < 18
            a: Простите, сервис доступен только для совершеннолетних
            go!: /CloseSession
        if: $session.data.channel === "whatsapp"
            go!: /Operator
        if: $session.data.channel === "widget"
            go!: Hello
        go!: /Start/CheckIsAuth

        state: CheckIsAuth
            if: !$session.data.isAuth
                go!: /Authorization/AskFIO
            a: Поздравляю, вы успешно авторизовались
            go!: /Start/Hello
        
        state: Hello
            a: Добро пожаловать, что вас интересует?
            if: $injector.isFailureNow || (testMode() && $session.testModeIsFailureNow)
                a: Простите, сервис временно не работает
                go!: /CloseSession
            a: <переход в основной сценарий бота>
    
    state: Operator
        a: Перевожу на оператора
        script: 
            $analytics.setComment("Перевод на оператора");

    state: CloseSession
        a: До свидания
        script:
            $analytics.setComment("Завершение сессии");
            $jsapi.stopSession();

    state: NoMatch || noContext = true
        event!: noMatch
        a: Хм, я вас не понял, попробуйте ещё раз, пожалуйста
