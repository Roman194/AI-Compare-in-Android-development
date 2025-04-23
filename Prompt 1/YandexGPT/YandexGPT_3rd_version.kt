/**
* Продолжаем модифицировать решение. Я напоминаю, что у нас уже есть enum class CupcakeScreen, который я отправлял 2 запроса назад. Другой enum class создавать не нужно. Также в функции 
* StartOrderScreen не хватает параметра onNextButtonClicked, который уже был реализован верно в первой версии кода. В функции SelectOptionsScreen в параметре options DataSource.flavors 
* предоставляет данные типа List<Int>, а требуются данные типа List<String>. Поэтому эти данные необходимо конвертировать в тип String и передать в функцию в этом формате. CupcakeAppTheme 
* является лишним и его нужно удалить.
*/

// Убираем лишний enum class, так как он уже существует

@Composable //Yandex prompt 1 3rd attempt
fun StartOrderScreen(
    onNextButtonClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Добро пожаловать в Cupcake App!",
            style = MaterialTheme.typography.h5,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Button(
            onClick = onNextButtonClicked,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Text("Начать заказ")
        }
    }
}

@Composable
fun SelectOptionsScreen(
    options: List<String>,
    onNextButtonClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Выберите опции для вашего кекса:",
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // Преобразуем список опций в String
        val stringOptions = options.map { it.toString() }
        
        // Отображаем список опций
        LazyColumn {
            items(stringOptions) { option ->
                Text(
                    text = option,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
        
        Button(
            onClick = onNextButtonClicked,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Далее")
        }
    }
}

@Composable
fun CupcakeApp(
    currentScreen: CupcakeScreen = CupcakeScreen.START
) {
    Column(modifier = Modifier.fillMaxSize()) {
        when (currentScreen) {
            CupcakeScreen.START -> {
                StartOrderScreen {
                    // Переход к следующему экрану
                }
            }
            CupcakeScreen.OPTIONS -> {
                SelectOptionsScreen(
                    options = DataSource.flavors.map { it.toString() },
                    onNextButtonClicked = {
                        // Переход к следующему экрану
                    }
                )
            }
        }
    }
}
