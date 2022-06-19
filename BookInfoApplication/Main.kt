// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.


@file:OptIn(ExperimentalMaterialApi::class)

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.imageio.ImageIO

fun loadImageFromUrl(urlStr: String): ImageBitmap {
    val url = URL(urlStr)
    val connection = url.openConnection() as HttpURLConnection
    connection.connect()

    val stream = ByteArrayOutputStream()
    ImageIO.write(ImageIO.read(connection.inputStream), "png", stream)
    val byteArray = stream.toByteArray()

    return org.jetbrains.skia.Image.makeFromEncoded(byteArray).asImageBitmap()
}

fun getInitialBookList(): MutableList<BookData> {
    return mutableListOf<BookData>(
        BookData("The Clean Code", "Robert C Martin", "813178696X", "https://images-na.ssl-images-amazon.com/images/I/81VctvTDc6L.jpg",  ""),
        BookData("PYTHON CRASH COURSE E02: A Hands-On, Project-Based Introduction to Programming", "Eric Matthes", "1593279280", "https://images-na.ssl-images-amazon.com/images/I/71NUZ+rHN2L.jpg", ""),
        BookData("Head First Design Patterns: Building Extensible and Maintainable Object-Oriented Software, Second Edition", " Eric Freeman and Elisabeth Robson", "9385889753", "https://images-na.ssl-images-amazon.com/images/I/71SdDjMglAL.jpg", ""),
        BookData("Design Patterns", "Erich Gamma", "9332555400", "https://images-na.ssl-images-amazon.com/images/I/81snQYegu6L.jpg", ""),
        BookData("Node.js Design Patterns: Design and implement production-grade Node.js applications using proven patterns and techniques, 3rd Edition", "Mario Casciaro and Luciano Mammino", "1785885588", "https://images-na.ssl-images-amazon.com/images/I/61hYzdT5WtL.jpg", "")
    )
}

@Composable
fun App() {
    var text by remember { mutableStateOf("Hello, World!") }
    var booksDetails by remember { mutableStateOf(mutableListOf<BookData>()) }
    val currentBookView = remember { mutableStateOf(getInitialBookList()[0]) }
    booksDetails = getInitialBookList()
    Box(modifier = Modifier.fillMaxWidth(1f).fillMaxHeight(1f)){
        Row(modifier = Modifier.background(Color.Cyan).fillMaxWidth(1f)){
            Column(modifier = Modifier.background(Color.White).weight(0.5f).fillMaxHeight(1f)){
                LazyColumn(modifier = Modifier){ items(booksDetails){
                    BookPreview(it, OnClick = {clickedBook ->
                        currentBookView.value = clickedBook
                        println("READ MORE clicked ${currentBookView.value.Title}")
                    })
                }}
            }
            Column(modifier = Modifier.background(Color.White).weight(0.5f).fillMaxHeight(1f)){
                BookView(currentBookView.value)
            }
        }
    }
}


@Composable
fun BookPreview(data: BookData, OnClick: (bookData: BookData) -> Unit){
    Card(modifier = Modifier.fillMaxWidth(1f).padding(vertical = 4.dp, horizontal = 10.dp).border(1.dp, Color.Gray).padding(vertical = 8.dp, horizontal = 4.dp), elevation = 0.dp){
        Column {
            Text(modifier = Modifier.padding(5.dp),style = TextStyle(Color.Black, 16.sp), text = data.Title!!)
            Text(modifier = Modifier.padding(5.dp),style = TextStyle(Color.DarkGray, 13.sp), text = data.AuthorName!!)
            Text(modifier = Modifier.padding(5.dp),style = TextStyle(Color.Gray, 9.sp), text = data.ISBN_number!!)
            Button(onClick = {OnClick(data)}){
                Text("View Book")
            }
        }
    }
}

@Composable
fun BookView(data: BookData){
    println("Loading Data for ${data.Title}")
    var img by remember { mutableStateOf(ImageBitmap(height = 100, width = 100)) }
    LaunchedEffect(key1 = data, block = {
        launch(Dispatchers.IO){
            println("Loading Data for ${data.imgURL!!}")
            img = loadImageFromUrl(data.imgURL!!)
        }
    })
    Column {
        Image(img, contentDescription = "Book Image", Modifier.weight(0.4f).align(Alignment.CenterHorizontally))
        Text(modifier = Modifier.padding(5.dp),style = TextStyle(Color.Black, 16.sp), text = data.Title!!)
        Text(modifier = Modifier.padding(5.dp),style = TextStyle(Color.DarkGray, 13.sp), text = data.AuthorName!!)
        Text(modifier = Modifier.padding(5.dp),style = TextStyle(Color.Gray, 9.sp), text = data.ISBN_number!!)
    }
}


fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Simple Window") {
        App()
    }
}


data class BookData(var Title: String? = null, var AuthorName: String? = null, var ISBN_number: String? = null, var imgURL: String? = null, var purchaseLink: String? = null)
