/*
 * Copyright 2016 Anton Tananaev (anton.tananaev@gmail.com)
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
 */
package org.traccar.manager;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class WebServiceCallback<T> implements Callback<T> {

    private Context context;

    public WebServiceCallback(Context context) {
        this.context = context;
    }

    public abstract void onSuccess(Response<T> response);

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (response.isSuccess()) {
            onSuccess(response);
        } else {
            CharSequence text = context.getResources().getText(R.string.error_general);
            Toast.makeText(context, text + ": " + response.message(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        CharSequence text = context.getResources().getText(R.string.error_connection);
        Toast.makeText(context, text + ": " + t.getMessage(), Toast.LENGTH_LONG).show();
        String stacktrace = Log.getStackTraceString(t) ;
        Log.d("onfailure_stack trace", stacktrace);
    }

}
