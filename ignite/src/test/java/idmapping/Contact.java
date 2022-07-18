package idmapping;

import java.util.HashMap;
import java.util.Map;

public class Contact {

    Map<String, String> identities = new HashMap<>(10);

    private boolean silenceAuth;
    private boolean youzan;
    private boolean webLogin;
    private boolean shouqianba;
    private boolean subscribe;
    private boolean wxaLogin;
    private boolean wxaRegister;

    public Map<String, String> getIdentities() {
        return identities;
    }

    public void setIdentities(Map<String, String> identities) {
        this.identities = identities;
    }

    public boolean isSilenceAuth() {
        return silenceAuth;
    }

    public void setSilenceAuth(boolean silenceAuth) {
        this.silenceAuth = silenceAuth;
    }

    public boolean isYouzan() {
        return youzan;
    }

    public void setYouzan(boolean youzan) {
        this.youzan = youzan;
    }

    public boolean isWebLogin() {
        return webLogin;
    }

    public void setWebLogin(boolean webLogin) {
        this.webLogin = webLogin;
    }

    public boolean isShouqianba() {
        return shouqianba;
    }

    public void setShouqianba(boolean shouqianba) {
        this.shouqianba = shouqianba;
    }

    public boolean isSubscribe() {
        return subscribe;
    }

    public void setSubscribe(boolean subscribe) {
        this.subscribe = subscribe;
    }

    public boolean isWxaLogin() {
        return wxaLogin;
    }

    public void setWxaLogin(boolean wxaLogin) {
        this.wxaLogin = wxaLogin;
    }

    public boolean isWxaRegister() {
        return wxaRegister;
    }

    public void setWxaRegister(boolean wxaRegister) {
        this.wxaRegister = wxaRegister;
    }
}
