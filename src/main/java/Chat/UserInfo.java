package Chat;

/**
 * @author qi.liu
 * @create 2018-11-01 16:40
 * @desc 描述:
 **/
public class UserInfo {

    private Long userId ;
    private String nickName ;

    private String ipInfo ;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getIpInfo() {
        return ipInfo;
    }

    public void setIpInfo(String ipInfo) {
        this.ipInfo = ipInfo;
    }
}
