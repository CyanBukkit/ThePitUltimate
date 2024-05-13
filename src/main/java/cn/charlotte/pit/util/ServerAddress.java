package cn.charlotte.pit.util;

import lombok.AllArgsConstructor;

/**
 * 2022/8/8<br>
 * ThePit-B<br>
 *
 * @author huanmeng_qwq
 */
@AllArgsConstructor
public enum ServerAddress {
    AC_CN("你的服务器名字", "你的群号");
    private String ip;
    private String qqGroup;

    public String displayIP() {
        return ip;
    }

    public String qqGroup() {
        return qqGroup;
    }
}
