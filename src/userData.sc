theme: /Authorization
    
    state: AskFIO
        a: Уточните, ваше ФИО — {{$session.data.client.name}} {{$session.data.client.sname}} {{$session.data.client.fname}}?
        
        state: No
            q: * ($no/$disagree/$disagreeStrong/$disagreeWeak) *
            a: Укажите вашу фамилию (напишите, пожалуйста, ТОЛЬКО фамилию)

            state: GetFName
                # можно было бы ограничить пользователя:
                # принимать только то, что попадает в сущность pymorphy.surn
                # но туда попадают не все фамилии (Швец не попадает), поэтому я не стала
                q: * 
                script: $session.data.client.fname = $request.query;
                a: Укажите ваше имя

                state: GetName
                    q: * @pymorphy.name *
                    script: $session.data.client.name = $parseTree["pymorphy.name"][0]["value"];
                    a: Укажите ваше отчество
                    
                    state: GetSName
                        q: * @pymorphy.patr *
                        script: $session.data.client.sname = $parseTree["pymorphy.patr"][0]["value"];
                        go!: /Authorization/AskFIO

        state: AskAge
            q: * ($yes/$agree/$agreeStrong/$agreeWeak) *
            a: Уточните, ваш возраст — {{$session.data.client.age}}?
            
            state: No
                q: * ($no/$disagree/$disagreeStrong/$disagreeWeak) *
                a: Укажите верный возраст
                
                state: GetAnswer
                    q: * @duckling.number *
                    script: $session.data.client.age = $parseTree["duckling.number"][0]["value"];
                    go!: /Authorization/AskFIO/AskAge

            state: AskPhoneNumber
                q: * ($yes/$agree/$agreeStrong/$agreeWeak) *
                a: Укажите номер телефона
                
                state: NoMatch
                    event: noMatch
                    script: $session.askPhoneCounter = $session.askPhoneCounter + 1 || 1; 
                    if: $session.askPhoneCounter > 2
                        go!: /Operator
                    a: Это не похоже на номер телефона, введите ещё раз
                    go: ..

                state: GenerateCode
                    q: * @duckling.phone-number *
                    script:
                        $session.code = generateCode();
                        $session.phone = $parseTree["duckling.phone-number"][0]["value"];
                    a: <отправили код ({{$session.code}}) на номер {{$session.phone}}>
                    a: Введите код из СМС

                    state: GetAnswer
                        q: *
                        if: Boolean($nlp.matchPatterns($request.query, ["* " + $session.code + " *"]))
                            # отправка данных в систему
                            script: sendUserData($injector.sendUserDataUrl, $session.data);
                            a: Поздравляю, вы успешно авторизовались
                            go!: /Start/Hello
                        script: $session.askCodeCounter = $session.askCodeCounter + 1 || 1; 
                        if: $session.askCodeCounter > 2
                            a: Вы не прошли авторизацию, попробуйте позже
                            go!: /CloseSession
                        a: Код не совпадает повторите попытку
                        go: ..