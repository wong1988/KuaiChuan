package io.github.wong1988.transmit.widget.server;

/**
 * 资源地址处理器
 */
public interface ResUriHandler {

    /**
     * 匹配规则
     */
    boolean matches(String uri);

    /**
     * 处理
     */
    void handler(HttpRequest request);

    /**
     * 销毁
     */
    void destroy();
}
