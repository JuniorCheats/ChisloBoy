package ru.study.mathpractice;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Locale;
import java.util.Random;

public class MainActivity extends Activity {

    private static final String KEY_FIRST = "first";
    private static final String KEY_SECOND = "second";
    private static final String KEY_OPERATOR = "operator";
    private static final String KEY_CORRECT_ANSWER = "correct_answer";
    private static final String KEY_CORRECT_COUNT = "correct_count";
    private static final String KEY_WRONG_COUNT = "wrong_count";
    private static final String KEY_EXAMPLE_ACTIVE = "example_active";
    private static final String KEY_ANSWER_CHECKED = "answer_checked";
    private static final String KEY_TYPED_ANSWER = "typed_answer";
    private static final String KEY_RESULT_TEXT = "result_text";

    private final Random random = new Random();

    private TextView exampleText;
    private EditText answerInput;
    private Button startButton;
    private Button checkButton;
    private TextView resultText;
    private TextView correctText;
    private TextView wrongText;
    private TextView percentText;

    private int firstNumber;
    private int secondNumber;
    private int correctAnswer;
    private int correctCount;
    private int wrongCount;
    private char operator;
    private boolean exampleActive;
    private boolean answerChecked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();

        startButton.setOnClickListener(v -> startNewExample());
        checkButton.setOnClickListener(v -> checkAnswer());

        if (savedInstanceState == null) {
            showInitialState();
        } else {
            restoreState(savedInstanceState);
        }

        updateStatistics();
    }

    private void findViews() {
        exampleText = findViewById(R.id.exampleText);
        answerInput = findViewById(R.id.answerInput);
        startButton = findViewById(R.id.startButton);
        checkButton = findViewById(R.id.checkButton);
        resultText = findViewById(R.id.resultText);
        correctText = findViewById(R.id.correctText);
        wrongText = findViewById(R.id.wrongText);
        percentText = findViewById(R.id.percentText);
    }

    private void showInitialState() {
        exampleText.setText(R.string.example_placeholder);
        exampleText.setBackgroundResource(R.drawable.example_default);
        answerInput.setText("");
        answerInput.setEnabled(false);
        startButton.setEnabled(true);
        checkButton.setEnabled(false);
        resultText.setText(R.string.press_start_hint);
        exampleActive = false;
        answerChecked = false;
    }

    private void startNewExample() {
        generateExample();

        exampleText.setText(firstNumber + " " + operator + " " + secondNumber + " = ?");
        exampleText.setBackgroundResource(R.drawable.example_default);
        answerInput.setText("");
        answerInput.setEnabled(true);
        answerInput.requestFocus();
        startButton.setEnabled(false);
        checkButton.setEnabled(true);
        resultText.setText(R.string.enter_answer_hint);
        exampleActive = true;
        answerChecked = false;
    }

    private void generateExample() {
        int operationNumber = random.nextInt(4);

        if (operationNumber == 0) {
            operator = '+';
            firstNumber = randomNumber();
            secondNumber = randomNumber();
            correctAnswer = firstNumber + secondNumber;
        } else if (operationNumber == 1) {
            operator = '-';
            firstNumber = randomNumber();
            secondNumber = randomNumber();
            correctAnswer = firstNumber - secondNumber;
        } else if (operationNumber == 2) {
            operator = '*';
            firstNumber = randomNumber();
            secondNumber = randomNumber();
            correctAnswer = firstNumber * secondNumber;
        } else {
            operator = '/';
            generateDivisionExample();
            correctAnswer = firstNumber / secondNumber;
        }
    }

    private int randomNumber() {
        return random.nextInt(90) + 10;
    }

    private void generateDivisionExample() {
        secondNumber = randomNumber();
        int maxMultiplier = 99 / secondNumber;
        int multiplier = random.nextInt(maxMultiplier) + 1;
        firstNumber = secondNumber * multiplier;
    }

    private void checkAnswer() {
        String value = answerInput.getText().toString().trim();

        if (value.isEmpty() || value.equals("-")) {
            answerInput.setError(getString(R.string.empty_answer_error));
            return;
        }

        int userAnswer;
        try {
            userAnswer = Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            answerInput.setError(getString(R.string.invalid_answer_error));
            return;
        }

        answerChecked = true;
        exampleActive = false;
        answerInput.setEnabled(false);
        checkButton.setEnabled(false);
        startButton.setEnabled(true);

        if (userAnswer == correctAnswer) {
            correctCount++;
            resultText.setText(R.string.correct_result);
            exampleText.setBackgroundResource(R.drawable.example_correct);
        } else {
            wrongCount++;
            resultText.setText(getString(R.string.wrong_result, correctAnswer));
            exampleText.setBackgroundResource(R.drawable.example_wrong);
        }

        updateStatistics();
    }

    private void updateStatistics() {
        int total = correctCount + wrongCount;
        double percent = total == 0 ? 0.0 : correctCount * 100.0 / total;

        correctText.setText(getString(R.string.correct_stat, correctCount));
        wrongText.setText(getString(R.string.wrong_stat, wrongCount));
        percentText.setText(String.format(Locale.US, "%.2f%%", percent));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(KEY_FIRST, firstNumber);
        outState.putInt(KEY_SECOND, secondNumber);
        outState.putChar(KEY_OPERATOR, operator);
        outState.putInt(KEY_CORRECT_ANSWER, correctAnswer);
        outState.putInt(KEY_CORRECT_COUNT, correctCount);
        outState.putInt(KEY_WRONG_COUNT, wrongCount);
        outState.putBoolean(KEY_EXAMPLE_ACTIVE, exampleActive);
        outState.putBoolean(KEY_ANSWER_CHECKED, answerChecked);
        outState.putString(KEY_TYPED_ANSWER, answerInput.getText().toString());
        outState.putString(KEY_RESULT_TEXT, resultText.getText().toString());
    }

    private void restoreState(Bundle state) {
        firstNumber = state.getInt(KEY_FIRST);
        secondNumber = state.getInt(KEY_SECOND);
        operator = state.getChar(KEY_OPERATOR);
        correctAnswer = state.getInt(KEY_CORRECT_ANSWER);
        correctCount = state.getInt(KEY_CORRECT_COUNT);
        wrongCount = state.getInt(KEY_WRONG_COUNT);
        exampleActive = state.getBoolean(KEY_EXAMPLE_ACTIVE);
        answerChecked = state.getBoolean(KEY_ANSWER_CHECKED);

        answerInput.setText(state.getString(KEY_TYPED_ANSWER, ""));
        resultText.setText(state.getString(KEY_RESULT_TEXT, getString(R.string.press_start_hint)));

        if (exampleActive || answerChecked) {
            exampleText.setText(firstNumber + " " + operator + " " + secondNumber + " = ?");
        } else {
            exampleText.setText(R.string.example_placeholder);
        }

        if (exampleActive) {
            exampleText.setBackgroundResource(R.drawable.example_default);
            answerInput.setEnabled(true);
            startButton.setEnabled(false);
            checkButton.setEnabled(true);
        } else if (answerChecked) {
            answerInput.setEnabled(false);
            startButton.setEnabled(true);
            checkButton.setEnabled(false);

            String result = resultText.getText().toString();
            if (result.equals(getString(R.string.correct_result))) {
                exampleText.setBackgroundResource(R.drawable.example_correct);
            } else {
                exampleText.setBackgroundResource(R.drawable.example_wrong);
            }
        } else {
            exampleText.setBackgroundResource(R.drawable.example_default);
            answerInput.setEnabled(false);
            startButton.setEnabled(true);
            checkButton.setEnabled(false);
        }
    }
}
