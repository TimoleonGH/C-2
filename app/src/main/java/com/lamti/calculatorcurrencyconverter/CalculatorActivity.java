package com.lamti.calculatorcurrencyconverter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.lamti.calculatorcurrencyconverter.retrofit_fixer.FixerObject;
import com.lamti.calculatorcurrencyconverter.retrofit_fixer.GetDataService;
import com.lamti.calculatorcurrencyconverter.retrofit_fixer.RetrofitClientInstance;

import org.mariuszgromada.math.mxparser.Expression;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CalculatorActivity extends AppCompatActivity implements View.OnClickListener {

    // Views
    private ConstraintLayout mRootCL, mInfoCL, mOutputCL, mMainButtons;
    private TextView mOutputTV, mInputTV;
    private ImageButton mSwitchIB;
    private Spinner mLeftSp, mRightSp;
    private ImageButton mAdditionIB, mSubtractionIB, mDivisionIB, mMultiplicationIB, mCardBackIB;
    private Button mOneB, mTwoB, mThreeB, mFourB, mFiveB, mSixB, mSevenB, mEightB, mNineB, mDotB, mZeroB, mEqualB, mDeleteB;
    private Button mCardOneB, mCardTwoB, mCardThreeB, mCardFourB, mCardFiveB, mCardSixB, mCardSevenB, mCardEightB, mCardNineB,
    mCardDotB, mCardZeroB;
    private View mRevealView;
    private FloatingActionButton mCardInfoFAB, mTapBottomSheetFAB;

    // BottomSheet
    private CoordinatorLayout mCoordinatorLayout;
    private View mBottomSheet;
    private BottomSheetBehavior<View> mBehavior;

    // Screen Dimensions vars
    private float mScreenHeight;
    private float mScreenWidth;

    // Spinners
    private ArrayList<String> mCurrencies = new ArrayList<>();
    private ArrayAdapter<String> mDataAdapter;

    // Color animations
    private AnimateColor mWhiteToAccent, mPrimaryDarkToWhite, mPrimaryToWhite2, mPrimaryToDark;

    // Math vars
    private Expression mMathExpression = new Expression("");
    private double mMathExpressionResult;
    private String mCurrentExpression = "";

    // Converter vars
    private String mSelectedCurrency = "";
    private Map<String, Double> mSortedRates;
    private boolean mGetRatesFlag = false;

    // Animation vars
    private float x, y, fabX, fabY;

    // TapTarget vars
    private int mTourStage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        screenDimensions();
        initializeViews();
        initializeBottomSheet();
    }

    @Override
    protected void onStart() {
        super.onStart();

        initializeColorAnimations();
        onDeleteLongClicked();
        setClickListeners();
        retrofitConverter();
        oneTimeTour();
    }


    // initializations
    private void screenDimensions() {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        mScreenHeight = displayMetrics.heightPixels;
        mScreenWidth = displayMetrics.widthPixels;
    }

    private void initializeViews() {

        // root views
        mRootCL = findViewById(R.id.rootCL);
        mMainButtons = findViewById(R.id.mainButtonsI);
        mTapBottomSheetFAB = findViewById(R.id.tapBottomSheetFAB);
        mRevealView = findViewById(R.id.revealView);
        mRevealView.setAlpha(0);

        // output views
        mDeleteB = findViewById(R.id.deleteB);
        mInputTV = findViewById(R.id.inputTV);
        mOutputTV = findViewById(R.id.outputTV);
        mOutputCL = findViewById(R.id.outputCL);

        // main buttons
        mOneB = findViewById(R.id.oneB);
        mTwoB = findViewById(R.id.twoB);
        mThreeB = findViewById(R.id.threeB);
        mFourB = findViewById(R.id.fourB);
        mFiveB = findViewById(R.id.fiveB);
        mSixB = findViewById(R.id.sixB);
        mSevenB = findViewById(R.id.sevenB);
        mEightB = findViewById(R.id.eightB);
        mNineB = findViewById(R.id.nineB);
        mDotB = findViewById(R.id.dotB);
        mZeroB = findViewById(R.id.zeroB);
        mEqualB = findViewById(R.id.equalB);
        mAdditionIB = findViewById(R.id.additionIB);
        mSubtractionIB = findViewById(R.id.subtractionIB);
        mDivisionIB = findViewById(R.id.divisionIB);
        mMultiplicationIB = findViewById(R.id.multiplicationIB);

        // card buttons
        mCardOneB = findViewById(R.id.cardOneB);
        mCardTwoB = findViewById(R.id.cardTwoB);
        mCardThreeB = findViewById(R.id.cardThreeB);
        mCardFourB = findViewById(R.id.cardFourB);
        mCardFiveB = findViewById(R.id.cardFiveB);
        mCardSixB = findViewById(R.id.cardSixB);
        mCardSevenB = findViewById(R.id.cardSevenB);
        mCardEightB = findViewById(R.id.cardEightB);
        mCardNineB = findViewById(R.id.cardNineB);
        mCardDotB = findViewById(R.id.cardDotB);
        mCardZeroB = findViewById(R.id.cardZeroB);
        mCardInfoFAB = findViewById(R.id.cardInfoFAB);
        mInfoCL = findViewById(R.id.infoCL);
        mCardBackIB = findViewById(R.id.cardBackIB);

        // converter views
        mLeftSp = findViewById(R.id.leftSp);
        mRightSp = findViewById(R.id.rightSp);
        mSwitchIB = findViewById(R.id.switchIB);

        hideConverterViews();
    }

    private void hideConverterViews() {

        // set views invisible
        mLeftSp.setScaleX(0f);
        mRightSp.setScaleX(0f);
        mSwitchIB.setScaleX(0f);
        mLeftSp.setScaleY(0f);
        mRightSp.setScaleY(0f);
        mSwitchIB.setScaleY(0f);
        mLeftSp.setClickable(false);
        mRightSp.setClickable(false);
        mLeftSp.setVisibility(View.GONE);
        mRightSp.setVisibility(View.GONE);
    }

    private void initializeBottomSheet() {

        mCoordinatorLayout = findViewById(R.id.rootCoordLayout);
        mBottomSheet = mCoordinatorLayout.findViewById(R.id.sheetCL);
        mBehavior = BottomSheetBehavior.from(mBottomSheet);

        mBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

                if ( newState == BottomSheetBehavior.STATE_EXPANDED ) {

                    if ( mCurrencies.isEmpty() ) {

                        // try again
                        if ( !retrofitConverter() ) {

                            mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                            Snackbar snackbar = Snackbar.make(mCoordinatorLayout, R.string.internetError, Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }

                    } else {
                        showConverterViews(true);
                    }

                } else {
                    showConverterViews(false);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

                animateViewsOnSwipe( slideOffset );
            }
        });
    }

    private void initializeColorAnimations() {

        mWhiteToAccent = new AnimateColor(
                ContextCompat.getColor(this, R.color.colorWhite),
                ContextCompat.getColor(this, R.color.colorAccent));

        mPrimaryDarkToWhite = new AnimateColor(
                ContextCompat.getColor(this, R.color.colorPrimaryDark),
                ContextCompat.getColor(this, R.color.colorWhite));

        mPrimaryToWhite2 = new AnimateColor(
                ContextCompat.getColor(this, R.color.colorPrimary),
                ContextCompat.getColor(this, R.color.colorWhite2));

        mPrimaryToDark = new AnimateColor(
                ContextCompat.getColor(this, R.color.colorPrimary),
                ContextCompat.getColor(this, R.color.colorPrimaryDark));
    }

    private void initializeSpinners() {

        // Spinner click listener
        /*mLeftSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String item = parent.getItemAtPosition(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/

        String[] table = {"EUR"};
        mDataAdapter = new ArrayAdapter<>(this, R.layout.custom_spinner_item, table);
        mDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mLeftSp.setAdapter(mDataAdapter);

        mRightSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                mSelectedCurrency = parent.getItemAtPosition(position).toString();

                if ( !String.valueOf(mMathExpressionResult).contains("NaN") ) {
                    calculateCurrencies();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mDataAdapter = new ArrayAdapter<>(this, R.layout.custom_spinner_item, mCurrencies);
        mDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mRightSp.setAdapter(mDataAdapter);

    }

    private void animateViewsOnSwipe ( float progress ) {

        float reverseProgress = 1 - progress;
        mMainButtons.setAlpha(reverseProgress);

        mOutputCL.setBackgroundColor(mWhiteToAccent.with(progress));
        mInputTV.setTextColor(mPrimaryDarkToWhite.with(progress));
        mOutputTV.setTextColor(mPrimaryToWhite2.with(progress));
        mDeleteB.setTextColor(mPrimaryDarkToWhite.with(progress));
    }

    private void oneTimeTour() {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        if ( !prefs.getBoolean("firstTime", false) ) {

            setTapTargetView(R.id.deleteB, R.color.colorWhite, R.color.colorAccent, R.color.colorPrimary,
                    R.color.colorPrimaryDark, R.color.colorPrimaryDark, getString(R.string.deleteTitle), getString(R.string.deleteDescription));

            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstTime", true).apply();
        }
    }

    private void setTapTargetView ( int targetViewID, int outerCircleColor, int  targetCircleColor, int descriptionTextColor,
                                    int textColor, int dimColor, String title, String description ) {

        TapTargetView.showFor(this,                 // `this` is an Activity
                TapTarget.forView(findViewById(targetViewID), title, description)
                        // All options below are optional
                        .outerCircleColor(outerCircleColor)      // Specify a color for the outer circle
                        .outerCircleAlpha(0.96f)            // Specify the alpha amount for the outer circle
                        .targetCircleColor(targetCircleColor)   // Specify a color for the target circle
                        .titleTextSize(20)                  // Specify the size (in sp) of the title text
                        .titleTextColor(targetCircleColor)      // Specify the color of the title text
                        .descriptionTextSize(16)            // Specify the size (in sp) of the description text
                        .descriptionTextColor(descriptionTextColor)  // Specify the color of the description text
                        .textColor(textColor)            // Specify a color for both the title and description text
                        .textTypeface(Typeface.SANS_SERIF)  // Specify a typeface for the text
                        .dimColor(dimColor)            // If set, will dim behind the view with 30% opacity of the given color
                        .drawShadow(true)                   // Whether to draw a drop shadow or not
                        .cancelable(false)                  // Whether tapping outside the outer circle dismisses the view
                        .tintTarget(true)                   // Whether to tint the target view's color
                        .transparentTarget(false)           // Specify whether the target is transparent (displays the content underneath)
                                           //.icon(Drawable)   Specify a custom drawable to draw as the target
                        .targetRadius(60),                  // Specify the target radius (in dp)
                new TapTargetView.Listener() {          // The listener can listen for regular clicks, long clicks or cancels
                    @Override
                    public void onTargetClick(TapTargetView view) {
                        super.onTargetClick(view);

                        view.dismiss(true);

                        if ( mTourStage == 0 ) {

                            mTourStage++;
                            mTapBottomSheetFAB.setVisibility(View.VISIBLE);

                            setTapTargetView(R.id.tapBottomSheetFAB, R.color.colorAccent, R.color.colorWhite,
                                    R.color.colorPrimary, R.color.colorWhite, R.color.colorWhite,
                                    getString(R.string.converterTitle), getString(R.string.converterDescription));

                        } else if ( mTourStage == 1 ) {

                            mTourStage++;
                            mTapBottomSheetFAB.setVisibility(View.GONE);
                        }
                    }
                });
    }


    // Actions
    private void setClickListeners() {

        // main buttons
        mOneB.setOnClickListener(this);
        mTwoB.setOnClickListener(this);
        mThreeB.setOnClickListener(this);
        mFourB.setOnClickListener(this);
        mFiveB.setOnClickListener(this);
        mSixB.setOnClickListener(this);
        mSevenB.setOnClickListener(this);
        mEightB.setOnClickListener(this);
        mNineB.setOnClickListener(this);
        mZeroB.setOnClickListener(this);
        mDotB.setOnClickListener(this);
        mEqualB.setOnClickListener(this);
        mDeleteB.setOnClickListener(this);
        mAdditionIB.setOnClickListener(this);
        mSubtractionIB.setOnClickListener(this);
        mDivisionIB.setOnClickListener(this);
        mMultiplicationIB.setOnClickListener(this);

        // converter buttons
        mCardOneB.setOnClickListener(this);
        mCardTwoB.setOnClickListener(this);
        mCardThreeB.setOnClickListener(this);
        mCardFourB.setOnClickListener(this);
        mCardFiveB.setOnClickListener(this);
        mCardSixB.setOnClickListener(this);
        mCardSevenB.setOnClickListener(this);
        mCardEightB.setOnClickListener(this);
        mCardNineB.setOnClickListener(this);
        mCardZeroB.setOnClickListener(this);
        mCardDotB.setOnClickListener(this);
        mCardInfoFAB.setOnClickListener(this);
        mSwitchIB.setOnClickListener(this);
        mCardBackIB.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch ( view.getId() ) {

            case R.id.oneB: mCurrentExpression += "1";
                expressionValidationCheck();
                break;
            case R.id.twoB: mCurrentExpression += "2";
                expressionValidationCheck();
                break;
            case R.id.threeB: mCurrentExpression += "3";
                expressionValidationCheck();
                break;
            case R.id.fourB: mCurrentExpression += "4";
                expressionValidationCheck();
                break;
            case R.id.fiveB: mCurrentExpression += "5";
                expressionValidationCheck();
                break;
            case R.id.sixB: mCurrentExpression += "6";
                expressionValidationCheck();
                break;
            case R.id.sevenB: mCurrentExpression += "7";
                expressionValidationCheck();
                break;
            case R.id.eightB: mCurrentExpression += "8";
                expressionValidationCheck();
                break;
            case R.id.nineB: mCurrentExpression += "9";
                expressionValidationCheck();
                break;
            case R.id.zeroB: mCurrentExpression += "0";
                expressionValidationCheck();
                break;
            case R.id.dotB: mCurrentExpression += ".";
                mInputTV.setText( mCurrentExpression );
                mMathExpression.setExpressionString( mCurrentExpression );
                break;
            case R.id.deleteB:
                deleteLastDigit();
                break;
            case R.id.additionIB:
                if ( operatorsValidationCheck() ) {
                    mCurrentExpression += "+";
                    mInputTV.setText(mCurrentExpression);
                }
                break;
            case R.id.subtractionIB:
                if ( operatorsValidationCheck() ) {
                    mCurrentExpression += "-";
                    mInputTV.setText(mCurrentExpression);
                }
                break;
            case R.id.multiplicationIB:
                if ( operatorsValidationCheck() ) {
                    mCurrentExpression += "*";
                    mInputTV.setText(mCurrentExpression);
                }
                break;
            case R.id.divisionIB:
                if ( operatorsValidationCheck() ) {
                    mCurrentExpression += "/";
                    mInputTV.setText(mCurrentExpression);
                }
                break;
            case R.id.equalB:
                equalAnimation();
                break;
            case R.id.switchIB:
                switchAnimation();
                break;
            case R.id.cardOneB: mCurrentExpression += "1";
                calculateCurrencies();
                break;
            case R.id.cardTwoB: mCurrentExpression += "2";
                calculateCurrencies();
                break;
            case R.id.cardThreeB: mCurrentExpression += "3";
                calculateCurrencies();
                break;
            case R.id.cardFourB: mCurrentExpression += "4";
                calculateCurrencies();
                break;
            case R.id.cardFiveB: mCurrentExpression += "5";
                calculateCurrencies();
                break;
            case R.id.cardSixB: mCurrentExpression += "6";
                calculateCurrencies();
                break;
            case R.id.cardSevenB: mCurrentExpression += "7";
                calculateCurrencies();
                break;
            case R.id.cardEightB: mCurrentExpression += "8";
                calculateCurrencies();
                break;
            case R.id.cardNineB: mCurrentExpression += "9";
                calculateCurrencies();
                break;
            case R.id.cardZeroB: mCurrentExpression += "0";
                calculateCurrencies();
                break;
            case R.id.cardDotB: mCurrentExpression += ".";
                mInputTV.setText( mCurrentExpression );
                break;
            case R.id.cardInfoFAB:
                cardAnimation();
                fabX = mCardInfoFAB.getX();
                fabY = mCardInfoFAB.getY();
                break;
            case R.id.cardBackIB:
                exitReveal(mCardBackIB, mInfoCL);
                break;
        }
    }

    private void calculateCurrencies() {

        mInputTV.setText( mCurrentExpression );
        mMathExpression.setExpressionString( mCurrentExpression );
        double inputNumber = mMathExpression.calculate();
        mMathExpressionResult = mSortedRates.get( mSelectedCurrency ) * inputNumber;
        if ( !String.valueOf(mMathExpressionResult).contains("NaN") ) {
            mOutputTV.setText( mMathExpressionResult + " " + mSelectedCurrency);
        }
    }

    private void deleteLastDigit() {

        if( mInputTV.getText().length() > 0 ) {

            mCurrentExpression = mCurrentExpression.substring(0, mCurrentExpression.length()-1);
            mInputTV.setText( mCurrentExpression );

            mMathExpression.setExpressionString( mCurrentExpression );
            calculateResult();
        }
    }

    private void deleteExpression() {

        mCurrentExpression = "";
        mInputTV.setText( mCurrentExpression );
        mMathExpression.setExpressionString( mCurrentExpression );
        mMathExpressionResult = 0;
        mOutputTV.setText("");
    }

    private void onDeleteLongClicked() {

        mDeleteB.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                enterReveal(mDeleteB, mRevealView);

                return true;
            }
        });
    }


    // Calculator Actions
    private boolean expressionValidationCheck() {

        mInputTV.setText( mCurrentExpression );
        mMathExpression.setExpressionString( mCurrentExpression );
        calculateResult();

        return true;
    }

    private void calculateResult() {

        try {
            mMathExpressionResult = mMathExpression.calculate();
            if ( !String.valueOf(mMathExpressionResult).contains("NaN") ) {
                mOutputTV.setText(String.valueOf(mMathExpressionResult));
            } else {
                mOutputTV.setText("");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean operatorsValidationCheck() {

        if ( mCurrentExpression.length() >= 1 ) {

            String lastDigit = mCurrentExpression.substring(mCurrentExpression.length()-1, mCurrentExpression.length());

            if ( lastDigit.contains("+") || lastDigit.contains("-") || lastDigit.contains("/") || lastDigit.contains("*") ) {
                mCurrentExpression = mCurrentExpression.substring(0, mCurrentExpression.length()-1);
            }

        }
        return true;
    }

    private void equalAction() {

        mCurrentExpression = String.valueOf( mMathExpressionResult );
        mMathExpression.setExpressionString(mCurrentExpression);
        mMathExpressionResult = 0;
        mInputTV.setText(mCurrentExpression);
        mOutputTV.setText("");
    }

    private void equalAnimation() {

        final float inputHeight = mInputTV.getMeasuredHeight();
        final float toY = - ( inputHeight / 2 );

        TranslateAnimation anim = new TranslateAnimation(0, 0, 0, -inputHeight);
        anim.setDuration(300);
        mInputTV.startAnimation(anim);
        TranslateAnimation anim2 = new TranslateAnimation(0, 0, 0,  toY);
        anim2.setDuration(300);
        mOutputTV.startAnimation(anim2);
        anim2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {


            }

            @Override
            public void onAnimationEnd(Animation animation) {

                equalAction();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }


    // Converter
    private boolean retrofitConverter() {

        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<FixerObject> call = service.getRates( getCurrentDate() );

        call.enqueue(new Callback<FixerObject>() {
            @Override
            public void onResponse(Call<FixerObject> call, Response<FixerObject> response) {

                HashMap<String,Double> rates = response.body().getRates();
                mSortedRates = new TreeMap<String, Double>(rates);

                if ( mSortedRates != null ) {
                    for (String key : mSortedRates.keySet()) {
                        mCurrencies.add(key);
                        mGetRatesFlag = true;
                    }

                    initializeSpinners();
                }
            }

            @Override
            public void onFailure(Call<FixerObject> call, Throwable t) {

            }
        });

        return mGetRatesFlag;
    }

    private String getCurrentDate() {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(System.currentTimeMillis());
    }

    private void switchAnimation() {

        mSwitchIB.animate().rotation(180).setDuration(300).setInterpolator(new AccelerateInterpolator())
                .withStartAction(new Runnable() {
                    @Override
                    public void run() {
                        mSwitchIB.setClickable(false);
                        animateSpinners();
                    }
                }).withEndAction(new Runnable() {
            @Override
            public void run() {
                mSwitchIB.setClickable(true);
                mSwitchIB.setRotation(0);
            }
        });
    }

    private void animateSpinners() {

        float animateValue = mSwitchIB.getY() - mLeftSp.getY();
        animateTranslation(mLeftSp, animateValue, 0, 150, new AccelerateInterpolator(), 0);
        animateTranslation(mRightSp, -animateValue, 0, 150, new AccelerateInterpolator(), 0);
    }

    private void showConverterViews(boolean boo) {

        if ( boo ) {
            if( mLeftSp.getScaleX() != 1.0 ) {

                mLeftSp.setClickable(true);
                mRightSp.setClickable(true);
                mLeftSp.setVisibility(View.VISIBLE);
                mRightSp.setVisibility(View.VISIBLE);

                animateScale(mLeftSp, 1.0f, 300, 75, new AnticipateOvershootInterpolator());
                animateScale(mRightSp, 1.0f, 300, 150, new AnticipateOvershootInterpolator());
                animateScale(mSwitchIB, 1.0f, 300, 0, new AnticipateOvershootInterpolator());
            }
        } else {
            if( mLeftSp.getScaleX() != 0.0 ) {
                animateScale(mLeftSp, 0f, 150, 75, new DecelerateInterpolator());
                animateScale(mRightSp, 0f, 150, 150, new DecelerateInterpolator());
                animateScale(mSwitchIB, 0f, 150, 0, new DecelerateInterpolator());
            }
        }
    }

    private void enableCardButtons ( boolean b, int visibility ) {

        mCardOneB.setClickable(b);
        mCardOneB.setVisibility(visibility);
        mCardTwoB.setClickable(b);
        mCardTwoB.setVisibility(visibility);
        mCardThreeB.setClickable(b);
        mCardThreeB.setVisibility(visibility);
        mCardFourB.setClickable(b);
        mCardFourB.setVisibility(visibility);
        mCardFiveB.setClickable(b);
        mCardFiveB.setVisibility(visibility);
        mCardSixB.setClickable(b);
        mCardSixB.setVisibility(visibility);
        mCardSevenB.setClickable(b);
        mCardSevenB.setVisibility(visibility);
        mCardEightB.setClickable(b);
        mCardEightB.setVisibility(visibility);
        mCardNineB.setClickable(b);
        mCardNineB.setVisibility(visibility);
        mDotB.setClickable(b);
        mDotB.setVisibility(visibility);
    }


    // Animations
    private void animateAlpha ( View view, float toAlpha, int duration, Interpolator interpolator ) {
        view.animate().alpha(toAlpha).setDuration(duration).setInterpolator(interpolator);
    }

    private void animateScale (View view, final float toScale, int duration, int delay, Interpolator interpolator ) {
        view.animate().scaleX(toScale).scaleY(toScale).setDuration(duration).setStartDelay(delay).setInterpolator(interpolator)
        .setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {

                if ( toScale == 0.0f ) {
                    mLeftSp.setClickable(false);
                    mRightSp.setClickable(false);
                    mLeftSp.setVisibility(View.GONE);
                    mRightSp.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    private void animateTranslation ( final View view, final float translateToX, final float translateToY, final int duration,
                                      final Interpolator interpolator, final int startDelay ){
        view.animate()
                .translationY(-translateToY)
                .translationX(-translateToX)
                .setDuration(duration)
                .setInterpolator(interpolator)
                .setStartDelay(startDelay)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {

                        view.animate()
                                .translationY(translateToY)
                                .translationX(translateToX)
                                .setDuration(duration)
                                .setInterpolator(interpolator)
                                .setStartDelay(startDelay)
                                .setListener(new Animator.AnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animator) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animator) {

                                    }

                                    @Override
                                    public void onAnimationCancel(Animator animator) {

                                    }

                                    @Override
                                    public void onAnimationRepeat(Animator animator) {

                                    }
                                });
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                });
    }

    private void enterReveal (final View animateView, final View revealView ) {

        int x = (int) animateView.getX();
        int y = (int) animateView.getY();

        int cx;
        int cy;
        Animator anim;

        if ( animateView.getId() == R.id.deleteB ) {
            cx = revealView.getWidth();
            cy = revealView.getHeight();
            float finalRadius = (float) Math.hypot(cx, cy);
            anim = ViewAnimationUtils.createCircularReveal(revealView, x, y, 0, finalRadius);
            revealView.setAlpha(1f);
        } else {
            cx = revealView.getWidth() / 2;
            cy = revealView.getHeight() / 2;
            float finalRadius = (float) Math.hypot(cx, cy);
            anim = ViewAnimationUtils.createCircularReveal(revealView, cx, cy, 0, finalRadius);
        }

        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

                switch (animateView.getId()) {

                    case R.id.deleteB:
                        break;
                    case R.id.cardInfoFAB:
                        revealView.setVisibility(View.VISIBLE);
                        animateView.setVisibility(View.INVISIBLE);
                        break;
                }
            }

            @Override
            public void onAnimationEnd(Animator animator) {

                switch (animateView.getId()) {

                    case R.id.deleteB:
                        if ( !mCurrentExpression.isEmpty() ) {
                            deleteExpression();
                        }
                        animateAlpha(revealView, 0f, 600, new LinearInterpolator());
                        break;
                    case R.id.cardInfoFAB:
                        animateView.setVisibility(View.INVISIBLE);
                        enableCardButtons(false, View.INVISIBLE);
                        break;
                }

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        anim.start();
    }

    private void exitReveal ( final View animateView, final View revealView ) {

        int cx = revealView.getWidth() / 2;
        int cy = revealView.getHeight() / 2;

        float initialRadius = (float) Math.hypot(cx, cy);

        Animator anim = ViewAnimationUtils.createCircularReveal(revealView, cx, cy, initialRadius, 0);

        anim.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);

                enableCardButtons(true, View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                ObjectAnimator objectAnimatorX2 = ObjectAnimator.ofFloat(mCardInfoFAB, "X", x, fabX);
                objectAnimatorX2.setInterpolator(new DecelerateInterpolator());

                ObjectAnimator objectAnimatorY2 = ObjectAnimator.ofFloat(mCardInfoFAB, "Y", y, fabY);

                objectAnimatorX2.setDuration(300);
                objectAnimatorY2.setDuration(300);

                objectAnimatorX2.start();
                objectAnimatorY2.start();

                revealView.setVisibility(View.INVISIBLE);
                mCardInfoFAB.setVisibility(View.VISIBLE);

            }
        });

        anim.start();
    }

    private void cardAnimation() {

        x = mInfoCL.getX() + (mInfoCL.getWidth()/2) - (mCardInfoFAB.getWidth()/2);
        y = mInfoCL.getY() + (mInfoCL.getHeight()/2) - (mCardInfoFAB.getHeight()/2);

        ObjectAnimator objectAnimatorX = ObjectAnimator.ofFloat(mCardInfoFAB, "X", x);
        ObjectAnimator objectAnimatorY = ObjectAnimator.ofFloat(mCardInfoFAB, "Y", y);
        objectAnimatorY.setInterpolator(new AccelerateInterpolator());

        objectAnimatorX.setDuration(300);
        objectAnimatorY.setDuration(300);

        objectAnimatorX.start();
        objectAnimatorY.start();

        objectAnimatorY.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onAnimationEnd(Animator animator) {

                enterReveal(mCardInfoFAB, mInfoCL);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }


    // Dimensions
    private int convertPxToDp(int px){
        return Math.round( px / (Resources.getSystem().getDisplayMetrics().xdpi/DisplayMetrics.DENSITY_DEFAULT) );
    }

    private int convertDpToPx(int dp){
        return Math.round( dp * (getResources().getDisplayMetrics().xdpi/DisplayMetrics.DENSITY_DEFAULT) );
    }

}