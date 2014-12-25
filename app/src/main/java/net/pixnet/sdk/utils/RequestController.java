package net.pixnet.sdk.utils;

import android.os.AsyncTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Control the request queue
 */
public class RequestController {

    private static RequestController INSTANCE;
    private static final int DEF_TASK_LIMIT = 3;
    private int taskLimit = DEF_TASK_LIMIT;

    private Queue<Request> requestQueue;
    private List<Worker> taskList;
    private ConnectionTool httpConn;

    /**
     * constructor
     * @return the instance of RequestController
     */
    synchronized
    public static RequestController getInstance(){
        if(INSTANCE==null)
            INSTANCE = new RequestController();
        return INSTANCE;
    }

    private RequestController(){
        requestQueue = new LinkedList<Request>();
        taskList = new ArrayList<Worker>();
    }

    /**
     * Set Tasks limits
     */
    public void setLimit(int taskLimit){
        this.taskLimit = taskLimit;
    }

    public void setHttpConnectionTool(ConnectionTool tool){
        httpConn=tool;
    }

    private HttpConnectionTool getHttpConnectionTool(){
        if(httpConn==null){
            httpConn = new HttpConnectionTool();
        }
        return (HttpConnectionTool) httpConn;
    }

    private int getTaskSize(){
        return taskList.size();
    }

    public void addRequest(Request request){
       if(requestQueue.offer(request)){
           runWorker();
       }
    }

    synchronized
    private void runWorker(){
        if(getTaskSize() < taskLimit){
            Request request=requestQueue.poll();
            if(request!=null){
                Worker worker=new Worker();
                taskList.add(worker);
                worker.execute(request);
            }
        }
    }

    private void onResponse(Request request, String response){
        Request.RequestCallback callback=request.getCallback();
        if(callback!=null)
            callback.onResponse(response);
        runWorker();
    }

    /**
     * Worker AsyncTask instead of Thread
     */
    private class Worker extends AsyncTask<Request, Void, String> {
        Request request;

        protected String doInBackground(Request... requestArray) {
            String response;
            request = requestArray[0];
            if (request != null) {
                try {
                    response=getHttpConnectionTool().performRequest(request);
                } catch (IOException e) {
                    e.printStackTrace();
                    response=null;
                }
            }
            else{
                response=null;
            }
            return response;
        }

        protected void onPostExecute(String response) {
            taskList.remove(this);
            onResponse(request, response);
        }
    }
}
