package edu.vassar.cs.lappsgrid.gost.http

import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response as OKResponse

/**
 *
 */
class OkClient implements HttpClient {
    private final OkHttpClient client = new OkHttpClient()
    String url

    OkClient(String url = HttpClient.SERVICE_URL) {
        this.url = url
    }


    Response post(String text) {
        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart('type', 'rest')
                .addFormDataPart('email', 'services@lappsgrid.org')
                .addFormDataPart('tagset', 'c7')
                .addFormDataPart('style', 'vert')
                .addFormDataPart('text', text)
                .build()

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build()

        OKResponse response = null;
        Response result = new Response()
        try {
            response = client.newCall(request).execute()
            result.status = response.code()
            result.message = response.message()
            result.body = response.body().string()
            result.contentType = response.body().contentType().toString()
        }
        finally {
            if (response) response.close()
        }
        return result
    }
}
