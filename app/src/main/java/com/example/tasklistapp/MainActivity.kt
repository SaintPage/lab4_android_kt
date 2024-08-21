package com.example.tasklistapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.tasklistapp.ui.theme.Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Theme {
                Screen()
            }
        }
    }
}

@Composable
fun Screen() {
    val tasks = remember { mutableStateListOf<Task>() }
    val context = LocalContext.current

    TaskListContent(
        tasks = tasks,
        onAddTask = { title, imageUrl ->
            // Validación: Evitar agregar elementos vacíos o duplicados
            if (title.isNotEmpty() && imageUrl.isNotEmpty()) {
                if (tasks.none { it.title == title && it.imageUri == imageUrl }) {
                    tasks.add(Task(title, imageUrl))
                } else {
                    Toast.makeText(context, "Duplicate entry not allowed", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Please enter both a title and an image URL", Toast.LENGTH_SHORT).show()
            }
        },
        onRemoveTask = { task ->
            tasks.remove(task)
        }
    )
}

@Composable
fun TaskListContent(
    tasks: List<Task>,
    onAddTask: (String, String) -> Unit,
    onRemoveTask: (Task) -> Unit
) {
    var newTaskTitle by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Recetas", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = newTaskTitle,
            onValueChange = { newTaskTitle = it },
            label = { Text("Nombre de la receta") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = imageUrl,
            onValueChange = { imageUrl = it },
            label = { Text("Ingresa URL de la imagen") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if (newTaskTitle.isNotEmpty() && imageUrl.isNotEmpty()) {
                    onAddTask(newTaskTitle, imageUrl)
                    newTaskTitle = ""
                    imageUrl = ""
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Green, // Cambia el color de fondo del botón a verde
                contentColor = Color.White    // Cambia el color del texto a blanco
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Agregar")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(tasks) { task ->
                TaskItem(task, onRemoveTask)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun TaskItem(task: Task, onRemoveTask: (Task) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onRemoveTask(task) },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = task.title,
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 20.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(task.imageUri)
                .crossfade(true)
                .error(android.R.drawable.ic_menu_report_image)
                .build(),
            contentDescription = null,
            modifier = Modifier.size(100.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Theme {
        Screen()
    }
}

data class Task(val title: String, val imageUri: String)
