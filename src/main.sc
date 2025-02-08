require: scripts.js
require: userData.sc
require: patterns.sc

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
        if: $session.data.client.age < 18
            a: Простите, сервис доступен только для совершеннолетних.
            go!: /CloseSession
        if: $session.data.channel === "wa"
            go!: /Operator
        if: $session.data.channel === "wg"
            go!: Hello
        go!: CheckIsAuth

        state: CheckIsAuth
            if: $session.data.isAuth
                a: Поздравляю, вы успешно авторизовались
                go!: /Start/Hello
        
        state: Hello
            a: Добро пожаловать, что вас интересует?
            if: $injector.isFailureNow
                a: Простите сервис временно не работает
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