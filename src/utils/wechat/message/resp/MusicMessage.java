package utils.wechat.message.resp;

/**
 * Created by yplyf on 2015/4/8.
 */
public class MusicMessage extends BaseMessage {
    // 音乐
    private Music Music;

    public Music getMusic() {
        return Music;
    }

    public void setMusic(Music music) {
        Music = music;
    }
}