function parseChannel(string) {
    var pattens = {
        "telegram*/tg": "tg",
        "whatsapp/whats app/wa": "wa",
        "widget/wid get": "wg"
    };
    var matchRes = $nlp.matchPatterns(string, _.keys(pattens));
    if (matchRes) return patterns[matchRes.pattern];
    // если вдруг не удастся определить канал или он какой-то неожиданный → виджет
    return "wg";
}