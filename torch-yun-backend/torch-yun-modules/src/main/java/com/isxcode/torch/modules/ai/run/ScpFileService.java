package com.isxcode.torch.modules.ai.run;

import com.isxcode.torch.api.cluster.dto.ScpFileEngineNodeDto;
import com.isxcode.torch.modules.ai.entity.AiEntity;
import com.isxcode.torch.modules.ai.repository.AiRepository;
import com.jcraft.jsch.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScpFileService {

    private final AiRepository aiRepository;

    @Async
    public void scpFile(ScpFileEngineNodeDto engineNode, String srcPath, String dstPath)
        throws JSchException, SftpException, InterruptedException, IOException {

        // 初始化jsch
        JSch jsch = new JSch();

        if (engineNode.getPasswd().length() > 1000) {
            jsch.addIdentity(engineNode.getUsername(), engineNode.getPasswd().getBytes(), null, null);
        }

        Session session =
            jsch.getSession(engineNode.getUsername(), engineNode.getHost(), Integer.parseInt(engineNode.getPort()));

        // 连接远程服务器
        if (engineNode.getPasswd().length() < 1000) {
            session.setPassword(engineNode.getPasswd());
        }

        session.setConfig("StrictHostKeyChecking", "no");
        session.setTimeout(10000);
        session.connect();

        // 上传文件
        ChannelSftp channel;
        channel = (ChannelSftp) session.openChannel("sftp");
        channel.connect(120000);

        try (InputStream inputStream = Files.newInputStream(Paths.get(srcPath))) {
            channel.put(inputStream, dstPath);
        }

        channel.disconnect();
        session.disconnect();
    }

    public boolean modelFileIsUpload(ScpFileEngineNodeDto clusterNode, String srcPath, String dstPath)
        throws JSchException, SftpException, IOException {

        // 初始化jsch
        JSch jsch = new JSch();

        if (clusterNode.getPasswd().length() > 1000) {
            jsch.addIdentity(clusterNode.getUsername(), clusterNode.getPasswd().getBytes(), null, null);
        }

        Session session =
            jsch.getSession(clusterNode.getUsername(), clusterNode.getHost(), Integer.parseInt(clusterNode.getPort()));

        // 连接远程服务器
        if (clusterNode.getPasswd().length() < 1000) {
            session.setPassword(clusterNode.getPasswd());
        }

        session.setConfig("StrictHostKeyChecking", "no");
        session.setTimeout(10000);
        session.connect();

        // 上传文件
        ChannelSftp channel;
        channel = (ChannelSftp) session.openChannel("sftp");
        channel.connect(120000);

        // 文件校验
        try (InputStream inputStream = Files.newInputStream(Paths.get(srcPath))) {
            SftpATTRS attrs = channel.stat(dstPath);
            long remoteFileSize = attrs.getSize();
            long localFileSize = inputStream.available();
            return remoteFileSize == localFileSize;
        } catch (SftpException e) {
            if ("No such file".equals(e.getMessage())) {
                return false;
            }
            throw e;
        } finally {
            channel.disconnect();
            session.disconnect();
        }

    }


    public void listenScpPercent(ScpFileEngineNodeDto engineNode, String srcPath, String dstPath, AiEntity ai)
        throws JSchException, IOException, InterruptedException {

        ai = aiRepository.findById(ai.getId()).get();

        // 初始化jsch
        JSch jsch = new JSch();

        if (engineNode.getPasswd().length() > 1000) {
            jsch.addIdentity(engineNode.getUsername(), engineNode.getPasswd().getBytes(), null, null);
        }

        Session session =
            jsch.getSession(engineNode.getUsername(), engineNode.getHost(), Integer.parseInt(engineNode.getPort()));

        // 连接远程服务器
        if (engineNode.getPasswd().length() < 1000) {
            session.setPassword(engineNode.getPasswd());
        }

        session.setConfig("StrictHostKeyChecking", "no");
        session.connect();

        // 初始化sftp功能
        ChannelSftp channel;
        channel = (ChannelSftp) session.openChannel("sftp");
        channel.connect(120000);

        // 文件校验
        SftpATTRS attrs;

        int scpPercent = 0;
        while (scpPercent < 100) {

            try {
                attrs = channel.stat(dstPath);
            } catch (Exception e) {
                log.debug("上传文件不存在，继续获取文件，不影响安装");
                continue;
            }

            try (InputStream inputStream = Files.newInputStream(Paths.get(srcPath))) {
                if (attrs != null) {
                    long remoteFileSize = attrs.getSize();
                    long localFileSize = inputStream.available();
                    scpPercent = (int) (remoteFileSize * 100 / localFileSize);
                }

                ai.setAiLog(ai.getAiLog() + "\n进度:" + scpPercent + "%");
                ai = aiRepository.saveAndFlush(ai);
            }

            Thread.sleep(10000);
        }

        channel.disconnect();
        session.disconnect();
    }
}
