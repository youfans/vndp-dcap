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

import java.io.IOException;
import java.io.InputStream;

/**
 * Author: 游锋锋
 * Time: 2016-02-25 20:09
 * Copyright (C) 2016 Xiamen Yaxon Networks CO.,LTD.
 */
public interface Resource {
	
	public InputStream getInputStream() throws IOException;
	
	public void close() ;

}
