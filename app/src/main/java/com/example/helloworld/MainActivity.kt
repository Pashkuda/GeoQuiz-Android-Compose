package com.example.helloworld

import android.os.Bundle
import android.view.Gravity
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.helloworld.ui.theme.HelloWorldTheme

// Модель запитання (Пункт 1)
data class Question(val textResId: Int, val answer: Boolean)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HelloWorldTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    QuizScreen()
                }
            }
        }
    }
}

@Composable
fun QuizScreen() {
    val context = LocalContext.current

    // База питань з випадковою вибіркою (Пункт 8)
    val questionBank = remember {
        listOf(
            Question(R.string.question_oceans, true),
            Question(R.string.question_mali, false),
            Question(R.string.question_ukraine, true),
            Question(R.string.question_everest, true)
        ).shuffled().take(4)
    }

    var currentIndex by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var isAnswered by remember { mutableStateOf(false) }
    var feedbackResId by remember { mutableIntStateOf(0) }
    var feedbackColor by remember { mutableStateOf(Color.Unspecified) }
    var quizCompleted by remember { mutableStateOf(false) }

    val currentQuestion = questionBank[currentIndex]

    // Відображення Toast у верхній частині екрана (Пункт 2)
    fun showTopToast(isCorrect: Boolean) {
        val messageRes = if (isCorrect) R.string.correct_toast else R.string.incorrect_toast
        val toast = Toast.makeText(context, messageRes, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.TOP, 0, 160)
        toast.show()
    }

    // Перевірка відповіді та зміна кольору (Пункти 3, 7)
    fun checkAnswer(userAnswer: Boolean) {
        if (isAnswered || quizCompleted) return

        val isCorrect = (userAnswer == currentQuestion.answer)
        if (isCorrect) {
            score++
            feedbackResId = R.string.correct_toast
            feedbackColor = Color(0xFF2E7D32) // Зелений колір
        } else {
            feedbackResId = R.string.incorrect_toast
            feedbackColor = Color(0xFFC62828) // Червоний колір
        }

        isAnswered = true
        showTopToast(isCorrect)
    }

    // Перехід до наступного запитання (Пункт 7)
    fun nextQuestion() {
        if (currentIndex < questionBank.size - 1) {
            currentIndex++
            isAnswered = false
            feedbackResId = 0
            feedbackColor = Color.Unspecified
        } else {
            quizCompleted = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Підрахунок балів (Пункт 7)
        Text(
            text = "Рахунок: $score / ${questionBank.size}",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        if (!quizCompleted) {
            // Виведення запитання або результату відповіді кольором (Пункт 3)
            Text(
                text = if (isAnswered && feedbackResId != 0) stringResource(id = feedbackResId) else stringResource(id = currentQuestion.textResId),
                fontSize = 22.sp,
                fontWeight = FontWeight.Medium,
                color = if (isAnswered) feedbackColor else MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(bottom = 16.dp)
            )

            // Кнопки відповідей (Пункт 1)
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                Button(
                    onClick = { checkAnswer(true) },
                    enabled = !isAnswered
                ) {
                    Text(text = stringResource(id = R.string.answer_true))
                }

                Button(
                    onClick = { checkAnswer(false) },
                    enabled = !isAnswered
                ) {
                    Text(text = stringResource(id = R.string.answer_false))
                }
            }

            // Графічна кнопка зі стрілкою переходу (Пункт 5)
            IconButton(
                onClick = { nextQuestion() },
                enabled = isAnswered,
                modifier = Modifier.size(56.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.arrow_right),
                    contentDescription = "Наступне питання",
                    modifier = Modifier.size(40.dp)
                )
            }
        } else {
            // Фінальний екран результатів
            Text(
                text = "Вікторину завершено!\nВаш результат: $score з ${questionBank.size}",
                fontSize = 22.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
        }
    }
}