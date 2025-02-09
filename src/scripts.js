function parseChannel(string) {
    var patterns = {
        "(telegram*/tg)": "telegram",
        "(whatsapp/whats app/wa)": "whatsapp",
        "(widget/wid get)": "widget"
    };
    var matchResult = $nlp.matchPatterns(string, _.keys(patterns));
    if (matchResult) return patterns[matchResult.pattern];
    // если вдруг не удастся определить канал или он какой-то неожиданный → виджет
    return "widget";
}

function generateCode() {
    // для тестирования
    if (testMode() && $.session.testModeAuthCode) return $.session.testModeAuthCode;
    var res = Math.floor(Math.random() * 9999).toString();
    while (res.length < 4) res = "0" + res;
    return res;
}

function sendUserData(url, sessionData) {
    var options = {
        // ну например, не важно
        dataType: "json",
        headers: {
            "Content-Type": "application/json"
        },
        body: {
            "timestamp": $jsapi.currentTime(),
            "client":
            {
                "name": sessionData.client.name,
                "sname": sessionData.client.sname,
                "fname": sessionData.client.fname,
                "age": sessionData.client.age
            },
            "channel": sessionData.channel
        }
    }
    var response = $http.post(url, options);
    // поскольку в сценарии не предусмотрена специальная обработка 
    // для неудачной попытки отправить данные в систему
    // я сделала тестовый вывод, чтобы можно было в автотестах отслеживать:
    // верное ли заполнение body получается
    if (testMode() && response.isOk) $reactions.answer("testMode: удалось отправить данные в систему");
    else if (testMode() && !response.isOk) $reactions.answer("testMode: не удалось отправить данные в систему");
}