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
package mobi.cangol.mobile.http.extras;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.util.EntityUtils;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class PollingResponseHandler{
    private final static String  TAG=PollingHttpClient.TAG;
	private final static boolean DEBUG=true;
	protected static final int START_MESSAGE = -1;
	protected static final int SUCCESS_MESSAGE = 0;
	protected static final int FAILURE_MESSAGE = 1;
	protected static final int FINISH_MESSAGE = 2;
	private Handler handler;
    public PollingResponseHandler() {
        if(Looper.myLooper() != null) {
            handler = new Handler(){
                public void handleMessage(Message msg){
                	PollingResponseHandler.this.handleMessage(msg);
                }
            };
        }
    }
    public boolean isFailResponse(String content){
        if(DEBUG)Log.d(TAG, "isFailResponse content="+content);
        return false;
    }
    public void onStart() {
    	if(DEBUG)Log.d(TAG, "onStart");
    }
    public void onPollingFinish(int execTimes, String content) {
    	if(DEBUG)Log.d(TAG, "onPollingFinish execTimes="+execTimes+" content:"+content);
    }
    public void onSuccess(int statusCode, String content) {
    	if(DEBUG)Log.d(TAG, "onSuccess statusCode="+statusCode+" content:"+content);
    }
    public void onFailure(Throwable error, String content) {
    	if(DEBUG)Log.d(TAG, "onFailure error="+error+" content:"+content);
    }
    public void sendStartMessage() {
    	sendMessage(obtainMessage(START_MESSAGE, new Object[]{new Integer(-1), "exec start"}));
    }
    public void sendFinishMessage(int execTimes) {
    	sendMessage(obtainMessage(FINISH_MESSAGE, new Object[]{new Integer(execTimes), "exec finish"}));
    }
    public void sendSuccessMessage(int statusCode, String responseBody) {
    	sendMessage(obtainMessage(SUCCESS_MESSAGE, new Object[]{new Integer(statusCode), responseBody}));
	}
	public void sendFailureMessage(IOException e, String responseBody) {
		 sendMessage(obtainMessage(FAILURE_MESSAGE, new Object[]{e, responseBody}));
	}
	boolean sendResponseMessage(HttpResponse response) {
        StatusLine status = response.getStatusLine();
        String responseBody = null;
        boolean result=false;
        try {
            HttpEntity entity = null;
            HttpEntity temp = response.getEntity();
            if(temp != null) {
                entity = new BufferedHttpEntity(temp);
                responseBody = EntityUtils.toString(entity, "UTF-8");
            }
        } catch(IOException e) {
            sendFailureMessage(e, (String) null);
            result=false;
        }

        if(status.getStatusCode() >= 300) {
            sendFailureMessage(new HttpResponseException(status.getStatusCode(), status.getReasonPhrase()), responseBody);
            result=false;
        } else {
        	if(isFailResponse(responseBody)){
        		sendFailureMessage(new HttpResponseException(status.getStatusCode(), status.getReasonPhrase()),responseBody);
                result=false;
            }else{
        		sendSuccessMessage(status.getStatusCode(), responseBody);
        		result=true;
        	}
        }
        return result;
    }
	protected void handleMessage(Message msg) {
		Object[] response;
    	 switch(msg.what) {
	    	 case SUCCESS_MESSAGE:
	             response = (Object[])msg.obj;
	             handleSuccessMessage(((Integer) response[0]).intValue(), (String) response[1]);
	             break;
	         case FAILURE_MESSAGE:
	             response = (Object[])msg.obj;
	             handleFailureMessage((Throwable)response[0], (String)response[1]);
	             break;
	         case FINISH_MESSAGE:
	             response = (Object[])msg.obj;
	             handleFinishMessage(((Integer) response[0]).intValue(), (String)response[1]);
	             break;
	         case START_MESSAGE:
	             handleStartMessage();
	             break;
    	 }
    }
	protected void handleStartMessage() {
		 onStart();
	}
	protected void handleFinishMessage(int execTimes, String responseBody) {
		onPollingFinish(execTimes, responseBody);
	}
    protected void handleSuccessMessage(int statusCode, String responseBody) {
        onSuccess(statusCode, responseBody);
    }

    protected void handleFailureMessage(Throwable e, String responseBody) {
        onFailure(e, responseBody);
    }
    protected void sendMessage(Message msg) {
        if(handler != null){
            handler.sendMessage(msg);
        } else {
            handleMessage(msg);
        }
    }
    protected Message obtainMessage(int responseMessage, Object response) {
        Message msg = null;
        if(handler != null){
            msg = this.handler.obtainMessage(responseMessage, response);
        }else{
            msg = new Message();
            msg.what = responseMessage;
            msg.obj = response;
        }
        return msg;
    }
}