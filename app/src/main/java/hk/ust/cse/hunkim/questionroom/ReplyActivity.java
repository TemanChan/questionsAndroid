package hk.ust.cse.hunkim.questionroom;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import hk.ust.cse.hunkim.questionroom.databinding.ActivityReplyBinding;
import hk.ust.cse.hunkim.questionroom.question.Question;
import hk.ust.cse.hunkim.questionroom.question.Reply;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class ReplyActivity extends Activity {

    private RESTfulAPI mAPI = RESTfulAPI.getInstance();
    private ActivityReplyBinding mBinding;
    private ReplyAdapter mReplyAdapter;
    private String mQuestionKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);

        Intent intent = getIntent();
        assert (intent != null);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_reply);
        mReplyAdapter = new ReplyAdapter(this, new ArrayList<Reply>());
        ListView replyListView = (ListView) findViewById(R.id.replyList);
        replyListView.setAdapter(mReplyAdapter);

        mQuestionKey = intent.getExtras().getString("questionKey");
        mAPI.getQuestion(mQuestionKey).enqueue(new Callback<Question>() {
            @Override
            public void onResponse(Response<Question> response, Retrofit retrofit) {
                Question question = response.body();
                if(question != null) {
                    mBinding.setQuestion(question);
                    mReplyAdapter.setReplyList(question.getReplies());
                }
                else {
                    Log.e("Empty Response Body", "Null Question");
                    //mBinding.setQuestion(new Question("", "all"));
                    mBinding.setQuestion(new Question("", "all",  "Anonymous", false));
                }
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });

        //Reply to the question
        Button replyButton = (Button) findViewById(R.id.replyButton);
        final EditText replyText = (EditText) findViewById(R.id.replyInput);

        replyButton.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View view) {
                        String replyContent = replyText.getText().toString();
                        ReplyActivity r = (ReplyActivity) view.getContext();
                        //Reply reply = new Reply(replyContent, mQuestionKey);
                        Reply reply = new Reply(replyContent, mQuestionKey, "Anonymous", false);
                        r.sendReply(reply);
                        replyText.setText("");
                    }
                }
        );
    }

    public void sendReply(Reply reply){
        mAPI.saveReply(reply).enqueue(new Callback<Reply>() {
            @Override
            public void onResponse(Response<Reply> response, Retrofit retrofit) {
                Reply reply = response.body();
                if (reply != null) {
                    mReplyAdapter.add(reply);
                }
                else {
                    Log.e("Empty Response Body", "Null reply");
                }
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

}
