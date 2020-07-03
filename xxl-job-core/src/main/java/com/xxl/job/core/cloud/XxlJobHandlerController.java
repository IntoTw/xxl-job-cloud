package com.xxl.job.core.cloud;

import com.xxl.job.core.biz.ExecutorBiz;
import com.xxl.job.core.biz.impl.ExecutorBizImpl;
import com.xxl.job.core.biz.model.*;
import com.xxl.job.core.util.GsonTool;
import com.xxl.job.core.util.ThrowableUtil;
import com.xxl.job.core.util.XxlJobRemotingUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;

/**
 * Copy from : https://github.com/xuxueli/xxl-rpc
 *
 * @author xuxueli 2020-04-11 21:25
 */
@Controller
public class XxlJobHandlerController {
    private static final Logger logger = LoggerFactory.getLogger(XxlJobHandlerController.class);

    private ExecutorBiz executorBiz;
    @Value("${xxl.job.accessToken:}")
    private String accessToken;
    @Autowired
    ThreadPoolExecutor bizThreadPool;
    @PostConstruct
    public void start() {
        executorBiz = new ExecutorBizImpl();
    }

    @PostMapping("/job/{method}")
    @ResponseBody
    public ReturnT jobHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, @PathVariable("method") String methodName) {
        return doHandlerReq(httpServletRequest,httpServletResponse,"/"+methodName);
    }
    // ---------------------- registry ----------------------

    protected ReturnT doHandlerReq(HttpServletRequest httpServletRequest,HttpServletResponse httpServletResponse,String method) {
        try {
            //read request
            int contentLength = httpServletRequest.getContentLength();
            byte[] reqBody=new byte[contentLength];
            httpServletRequest.getInputStream().read(reqBody,0,contentLength);
            String requestData=new String(reqBody, StandardCharsets.UTF_8);
            String uri = method;
            String accessTokenReq = httpServletRequest.getHeader(XxlJobRemotingUtil.XXL_JOB_ACCESS_TOKEN);
            FutureTask<ReturnT> stringFutureTask=new FutureTask<ReturnT>(() -> process(uri, requestData, accessTokenReq));
            // invoke
            bizThreadPool.execute(stringFutureTask);
            ReturnT returnT = stringFutureTask.get();
            httpServletResponse.setHeader(HttpHeaders.CONTENT_TYPE, "text/html;charset=UTF-8");


            return returnT;
        } catch (Exception e) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "invalid request, uri-mapping empty.");
        }
    }

    private ReturnT process(String uri, String requestData, String accessTokenReq) {

        if (uri == null || uri.trim().length() == 0) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "invalid request, uri-mapping empty.");
        }
        if (accessToken != null
                && accessToken.trim().length() > 0
                && !accessToken.equals(accessTokenReq)) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "The access token is wrong.");
        }

        // services mapping
        try {
            if ("/beat".equals(uri)) {
                return executorBiz.beat();
            } else if ("/idleBeat".equals(uri)) {
                IdleBeatParam idleBeatParam = GsonTool.fromJson(requestData, IdleBeatParam.class);
                return executorBiz.idleBeat(idleBeatParam);
            } else if ("/run".equals(uri)) {
                TriggerParam triggerParam = GsonTool.fromJson(requestData, TriggerParam.class);
                return executorBiz.run(triggerParam);
            } else if ("/kill".equals(uri)) {
                KillParam killParam = GsonTool.fromJson(requestData, KillParam.class);
                return executorBiz.kill(killParam);
            } else if ("/log".equals(uri)) {
                LogParam logParam = GsonTool.fromJson(requestData, LogParam.class);
                return executorBiz.log(logParam);
            } else {
                return new ReturnT<String>(ReturnT.FAIL_CODE, "invalid request, uri-mapping(" + uri + ") not found.");
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new ReturnT<String>(ReturnT.FAIL_CODE, "request error:" + ThrowableUtil.toString(e));
        }
    }

    /**
     * write response
     */
    private void writeResponse(ChannelHandlerContext ctx, boolean keepAlive, String responseJson) {
        // write response
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.copiedBuffer(responseJson, CharsetUtil.UTF_8));   //  Unpooled.wrappedBuffer(responseJson)
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html;charset=UTF-8");       // HttpHeaderValues.TEXT_PLAIN.toString()
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        if (keepAlive) {
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }
        ctx.writeAndFlush(response);
    }


}
