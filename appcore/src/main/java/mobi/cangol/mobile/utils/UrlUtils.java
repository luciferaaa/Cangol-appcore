/** 
 * Copyright (c) 2013 Cangol
 * 
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mobi.cangol.mobile.utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Cangol
 */
public class UrlUtils {

	/**
	 * 判断是否是url
	 * 
	 * @param value
	 * @return
	 */
	public static boolean isUrl(String value) {
		if (value != null&&!"".equals(value)) {
			return value
					.matches("(((http|ftp|https|file)://)?([\\w\\-]+\\.)+[\\w\\-]+(/[\\w\\u4e00-\\u9fa5\\-\\./?\\@\\%\\!\\&=\\+\\~\\:\\#\\;\\,]*)?)");
		} else
			return false;
	}

	/**
	 * 从url获取主机
	 * 
	 * @param url
	 * @return
	 */
	public static String getHost(String url) {

		try {
			return new URL(url).getHost();
		} catch (MalformedURLException e) {
			return "";
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	/**
	 * 从url获取参数map
	 * 
	 * @param url
	 * @return Map
	 */
	public static Map<String, String> getParams(String url) {

		String query = "";
		try {
			query = new URL(url).getQuery();
		} catch (MalformedURLException e) {
			query = "";
		}

		Map<String, String> queries = new HashMap<String, String>();
		if (query == null) {
			return queries;
		}

		for (String entry : query.split("&")) {
			String[] keyvalue = entry.split("=");
			if (keyvalue.length != 2) {
				continue;
			}
			queries.put(keyvalue[0], keyvalue[1]);
		}
		return queries;
	}
}
