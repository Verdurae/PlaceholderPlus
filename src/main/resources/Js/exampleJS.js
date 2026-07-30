identifier = "exampleJS"
author = "Kaminy"
version = "1.1.5"

function onRequest(player, params) {
    // 示例处理 如果这个玩家是作者的话返回"这个玩家是作者"
    // 你可以写任意多的判断在这里
    // params是这个调用的js的参数，形如%exampleJS_参数1_参数2_参数3%，该方法获得的params就是一整段"参数1_参数2_参数3"作为一个参数，需要手动使用_切割
    if (player.player.getName() === author) {
        return "这个玩家是作者";
    } else {
        return "这个玩家不是作者";
    }
}