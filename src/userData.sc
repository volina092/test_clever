theme: /Authorization
    
    state: AskFIO
        a: Уточните, ваше ФИО — {{$.request.client.name}} {{$.request.client.sname}} {{$.request.client.fname}}?
        
        state: No
            q: нет
            a: Уточните ваше ФИО
        state: AskAge
            a: Уточните ваш возраст
            
            state: AskPhoneNumber
                a: Укажите номер телефона
                
                state: GenerateCode

                    state: GetAnswer