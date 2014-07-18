package net.pixnet.sdk;

import android.app.AlertDialog;
import android.content.Context;
import android.view.WindowManager;
import android.webkit.WebView;

import net.pixnet.sdk.utils.Helper;
import net.pixnet.sdk.utils.OAuthHelper;
import net.pixnet.sdk.utils.OAuthLoginHelper;
import net.pixnet.sdk.utils.Request;

import org.json.JSONException;
import org.json.JSONObject;



public class PIXNET {
    public static void oAuth2Login(final Context context, final OnAccessTokenGotListener listener,String redirectUrl){
        WebView webView = new WebView(context);
        final AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(webView)
                .create();
        dialog.show();
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        OAuthHelper oh = new OAuthHelper(OAuthHelper.OAuthVersion.VER_2,context.getString(R.string.consumer_key),context.getString(R.string.consumer_secret));
        oh.setRedirect_uri(redirectUrl);
        oh.login2(webView, new Request.RequestCallback() {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();
                try {
                    JSONObject job = new JSONObject(response);
                    listener.onAccessTokenGot(job.getString("access_token"), "OAuth2");
                }catch(JSONException e){

                }

            }
        });
    }
    public static void oAuth1Login(final Context context, final OnAccessTokenGotListener listener){
        WebView webView=new WebView(context);
        final AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(webView)
                .create();

        OAuthLoginHelper loginHelper=new OAuthLoginHelper(context.getString(R.string.consumer_key), context.getString(R.string.consumer_secret));
        loginHelper.setOAuthLoginListener(new OAuthLoginHelper.OAuthLoginListener() {
            @Override
            public void onRequestUrlGot() {
                Helper.log("onRequestUrlGot");
            }

            @Override
            public void onVerify() {
                Helper.log("onVerify");
            }

            @Override
            public void onAccessTokenGot(String token, String secret) {
                Helper.log("onAccessTokenGot");
                listener.onAccessTokenGot(token, secret);
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

        loginHelper.login(webView);
    }

    public interface OnAccessTokenGotListener{
        void onAccessTokenGot(String token, String secret);
    }

}
