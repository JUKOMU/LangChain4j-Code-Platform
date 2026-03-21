package io.github.jukomu.langchain4jcodeplatform.saver;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import io.github.jukomu.langchain4jcodeplatform.constant.AppConstant;
import io.github.jukomu.langchain4jcodeplatform.exception.ErrorCode;
import io.github.jukomu.langchain4jcodeplatform.model.enums.CodeGenTypeEnum;
import io.github.jukomu.langchain4jcodeplatform.util.ThrowUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * @author JUKOMU
 * @Description: 文件保存模板
 * @Project: LangChain4j-Code-Platform
 * @Date: 2026/3/18
 */
public abstract class CodeFileSaverTemplate<T> {

    // 文件保存根目录
    protected static final String FILE_SAVE_ROOT_DIR = AppConstant.CODE_OUTPUT_ROOT_DIR;

    // 应用生成根目录 (用于浏览)
    protected static final String PREVIEW_ROOT_DIR = AppConstant.CODE_OUTPUT_ROOT_DIR;

    /**
     * 模板方法：保存代码的标准流程
     *
     * @param result 代码结果对象
     * @param appId  应用 id
     * @return 保存的目录
     */
    public final File saveCode(T result, Long appId) {
        // 1. 验证输入
        validateInput(result);
        // 2. 构建唯一目录
        String baseDirPath = buildUniqueDir(appId);
        // 3. 保存文件（具体实现由子类提供）
        saveFiles(result, baseDirPath);
        // 4. 返回目录文件对象
        return new File(baseDirPath);
    }

    /**
     * 验证输入参数（可由子类覆盖）
     *
     * @param result 代码结果对象
     */
    protected void validateInput(T result) {
        ThrowUtils.throwIf(result == null, ErrorCode.SYSTEM_ERROR, "代码结果对象不能为空");
    }

    /**
     * 构建唯一目录路径
     *
     * @return 目录路径
     */
    protected final String buildUniqueDir() {
        String codeType = getCodeType().getValue();
        String uniqueDirName = StrUtil.format("{}_{}", codeType, IdUtil.getSnowflakeNextIdStr());
        String dirPath = FILE_SAVE_ROOT_DIR + File.separator + uniqueDirName;
        FileUtil.mkdir(dirPath);
        return dirPath;
    }

    /**
     * 构建基于 appId 的唯一路径
     *
     * @param appId 应用 id
     * @return
     */
    protected final String buildUniqueDir(Long appId) {
        ThrowUtils.throwIf(appId == null, ErrorCode.PARAMS_ERROR, "应用ID不能为空");
        String codeType = getCodeType().getValue();
        String uniqueDirName = StrUtil.format("{}_{}", codeType, appId);
        String dirPath = FILE_SAVE_ROOT_DIR + File.separator + uniqueDirName;
        FileUtil.mkdir(dirPath);
        return dirPath;
    }

    /**
     * 写入单个文件的工具方法
     *
     * @param dirPath  目录路径
     * @param filename 文件名
     * @param content  文件内容
     */
    protected final void writeToFile(String dirPath, String filename, String content) {
        if (StrUtil.isNotBlank(content)) {
            String filePath = dirPath + File.separator + filename;
            FileUtil.writeString(content, filePath, StandardCharsets.UTF_8);
        }
    }

    /**
     * 获取代码类型（由子类实现）
     *
     * @return 代码生成类型
     */
    protected abstract CodeGenTypeEnum getCodeType();

    /**
     * 保存文件的具体实现（由子类实现）
     *
     * @param result      代码结果对象
     * @param baseDirPath 基础目录路径
     */
    protected abstract void saveFiles(T result, String baseDirPath);
}

