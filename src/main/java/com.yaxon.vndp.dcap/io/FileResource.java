/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.yaxon.vndp.dcap.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;

/**
 * Author: 游锋锋
 * Time: 2016-02-25 20:09
 * Copyright (C) 2016 Xiamen Yaxon Networks CO.,LTD.
 */
public class FileResource implements Resource {
    protected static Logger logger = LoggerFactory.getLogger(FileResource.class);
    public static final String CLASS_PATH_PREFIX = "classpath:";

    protected File file;

    protected String classPath;

    InputStream fis = null;

    protected final boolean streamResource;

    public FileResource(File f) {
        this.file = f;
        this.streamResource = false;
    }

    /**
     * 支持以classpath:开头的路径和文件的绝对路径2种方式*
     */
    public FileResource(String fileName) {
        this(null, fileName);
    }

    public FileResource(Resource relatedResource, String fileName) {
        if (fileName.startsWith(CLASS_PATH_PREFIX)) {
            initClassPathFile(fileName);
            this.streamResource = true;
        } else {
            if (relatedResource == null) {
                this.file = new File(fileName);
            } else if (relatedResource instanceof FileResource) {
                File relatedFile = ((FileResource) relatedResource).getFile();

                File f = new File(fileName);
                if (f.isAbsolute()) {
                    this.file = f;
                } else {
                    this.file = new File(relatedFile.getParentFile(), fileName);
                }
            } else {
                logger.warn("unknown relatived Resource:" + relatedResource);
            }

            this.streamResource = false;
        }
    }

    @SuppressWarnings("deprecation")
    protected void initClassPathFile(String classpathFile) {
        this.classPath = classpathFile.substring(CLASS_PATH_PREFIX.length());

        String classRootPath = FileResource.class.getResource("/").getFile();
        String m_fileName = classpathFile.substring(CLASS_PATH_PREFIX.length());

        //This warning is not reasonable. The file encoding should be the native one in this situation.
        this.file = new File(URLDecoder.decode(classRootPath), m_fileName);
    }

    public void close() {
        if (fis != null) {
            try {
                fis.close();
            } catch (IOException e) {
                logger.error("error on close the inputstream.", e);
            }
        }
    }

    public InputStream getInputStream() throws IOException {
        if (fis == null) {
            if (isStreamResource()) {
                fis = FileResource.class.getClassLoader().getResourceAsStream(this.classPath);
            } else {
                fis = new FileInputStream(file);
            }
        }

        if (fis == null) {
            //must be failed stream resource, OR FileInputStream will raise a exception above.
            throw new IOException("resource is not available. file is:" + this.classPath);
        }

        return fis;
    }

    public String toString() {
        return "file resource. file is:" + (this.file == null ? this.classPath : this.file.getAbsolutePath());
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public boolean isStreamResource() {
        return streamResource;
    }

}
