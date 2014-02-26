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
package mobi.cangol.mobile.service.conf;

import java.io.File;

import mobi.cangol.mobile.service.Service;
import mobi.cangol.mobile.service.ServiceProperty;
import mobi.cangol.mobile.utils.StorageUtils;
import android.content.Context;
@Service("ConfigService")
/**
 * @Description ConfigServiceImpl.java 
 * @author Cangol
 * @date 2013-12-28
 */
class ConfigServiceImpl implements ConfigService {
	private Context mContext = null;
	private ServiceProperty mServiceProperty=null;
	private boolean mDebug=false;
	@Override
	public void onCreate(Context context) {
		mContext=context;
	}
	@Override
	public void setDebug(boolean debug) {
		mDebug=debug;
	}
	@Override
	public void init(ServiceProperty serviceProperty) {
		this.mServiceProperty=serviceProperty;
	}

	@Override
	public ServiceProperty getServiceProperty() {
		return mServiceProperty;
	}
	@Override
	public String getName() {
		return "ConfigService";
	}

	@Override
	public void onDestory() {
		
	}
	
	@Override
	public String getAppDir() {
		return StorageUtils.getExternalStorageDir(mContext, mServiceProperty.getString(ConfigService.APP_DIR,mContext.getApplicationInfo().name));
	}

	@Override
	public String getCacheDir() {
		return StorageUtils.getExternalCacheDir(mContext).getAbsolutePath();
	}

	@Override
	public String getImageDir() {
		return getAppDir()+File.separator+mServiceProperty.getString(ConfigService.IMAGE_DIR);
	}

	@Override
	public String getTempDir() {
		return getAppDir()+File.separator+mServiceProperty.getString(ConfigService.TEMP_DIR);
	}

	@Override
	public String getDownloadDir() {
		return getAppDir()+File.separator+mServiceProperty.getString(ConfigService.DOWNLOAD_DIR);
	}
	
	@Override
	public String getUpgradeDir() {
		return getAppDir()+File.separator+mServiceProperty.getString(ConfigService.UPGRADE_DIR);
	}
	
	@Override
	public String getDatabaseName() {
		return mServiceProperty.getString(ConfigService.DATABASE_NAME);
	}

	@Override
	public String getSharedName() {
		return mServiceProperty.getString(ConfigService.SHARED_NAME);
	}
}
