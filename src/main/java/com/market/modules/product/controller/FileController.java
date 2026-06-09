package com.market.modules.product.controller;

import cn.hutool.core.util.IdUtil;
import com.market.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * 文件上传控制器
 *
 * 正式环境建议替换为阿里云 OSS / MinIO 上传
 */
@Slf4j
@Tag(name = "文件接口", description = "图片上传")
@RestController
@RequestMapping("/api/v1/files")
public class FileController {

    /** 允许的图片格式 */
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif", "webp");

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @Operation(summary = "上传图片（支持多文件）")
    @PostMapping("/upload")
    public Result<List<String>> upload(@RequestParam("files") MultipartFile[] files) {
        if (files == null || files.length == 0) {
            return Result.fail("请选择文件");
        }

        List<String> urls = new ArrayList<>();

        for (MultipartFile file : files) {
            // 检查文件类型
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) continue;

            String ext = getExtension(originalFilename).toLowerCase();
            if (!ALLOWED_EXTENSIONS.contains(ext)) {
                return Result.fail("不支持的文件格式: " + ext);
            }

            // 检查文件大小 (5MB)
            if (file.getSize() > 5 * 1024 * 1024) {
                return Result.fail("文件过大，最大支持 5MB");
            }

            try {
                // 生成唯一文件名
                String fileName = IdUtil.fastSimpleUUID() + "." + ext;

                // 按日期分目录
                String dateDir = java.time.LocalDate.now().toString().replace("-", "/");
                Path dirPath = Paths.get(uploadDir, dateDir);
                Files.createDirectories(dirPath);

                // 保存文件
                Path filePath = dirPath.resolve(fileName);
                file.transferTo(filePath.toFile());

                // 返回相对路径（前端拼接域名）
                String url = "/uploads/" + dateDir + "/" + fileName;
                urls.add(url);

                log.info("文件上传成功: {}", url);

            } catch (IOException e) {
                log.error("文件上传失败", e);
                return Result.fail("文件上传失败: " + e.getMessage());
            }
        }

        return Result.ok(urls);
    }

    private String getExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        return dotIndex == -1 ? "" : filename.substring(dotIndex + 1);
    }
}
