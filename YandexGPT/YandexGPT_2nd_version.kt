/**
* Модифицируй пожалуйста предыдущее решение. Реализованным в рамках навигации Composable-функциям не хватает ряда параметров. Функция StartOrderScreen должна иметь параметр quatityOptions типа 
* List<Pair<Int, Int>>, функция SelectOptionScreen - параметр subtotal типа String и параметр onSelectionChanged: (String) -> Unit, не иметь параметр title, а функция OrderSummaryScreen - 
* параметр orderUiState вместо order, параметр onSendButtonClicked вместо onConfirmButtonClicked. Для всех трёх написанных выше функций необходимо добавить параметр modifier.

* Обрати также внимание на то, что в onSendButtonClicked ожидается 2 параметра типа String, а не 1. Первый с названием subject, а второй - summary. Обрати внимание на переменную uiState! Она 
* не лишняя и используется в параметрах options (за исключением SelectOptionScreen где route flavour, там используются данные из DataSource), subtotal и orderUiState.

* innerPadding -> нигде не используется, поэтому его необходимо удалить. В функции CupcakeApp не хватает переменных backStackEntry и currentScreen. Последняя в зависимости от текущего элемента 
* из enum class-а меняет значение тайтла в CupcakeAppBar и поэтому должна быть передана в эту функцию

* Ниже прикрепляю объект DataSorce и data class OrderUiState, которые должны помочь тебе доработать код.
*/

@Composable //Yandex prompt 1 2nd attempt
fun CupcakeApp(
    navController: NavHostController,
    backStackEntry: NavBackStackEntry,
    currentScreen: (Route) -> Unit
) {
    val uiState = backStackEntry.savedStateHandle.get<OrderUiState>("uiState")
    val scope = rememberCoroutineScope()
    
    CupcakeAppTheme {
        NavHost(
            navController = navController,
            startDestination = Route.START.route
        ) {
            composable(Route.START.route) {
                StartOrderScreen(
                    modifier = Modifier.fillMaxSize(),
                    quantityOptions = DataSource.quantityOptions,
                    onQuantitySelected = { quantity ->
                        scope.launch {
                            backStackEntry.savedStateHandle.set(
                                "uiState",
                                uiState?.copy(quantity = quantity) ?: OrderUiState(quantity = quantity)
                            )
                            navController.navigate(Route.FLAVOR.route)
                        }
                    }
                )
                currentScreen(Route.START)
            }
            
            composable(Route.FLAVOR.route) {
                SelectOptionScreen(
                    modifier = Modifier.fillMaxSize(),
                    options = DataSource.flavors,
                    subtotal = uiState?.price ?: "",
                    onSelectionChanged = { flavor ->
                        scope.launch {
                            backStackEntry.savedStateHandle.set(
                                "uiState",
                                uiState?.copy(flavor = flavor)
                            )
                        }
                    }
                )
                currentScreen(Route.FLAVOR)
            }
            
            composable(Route.SUMMARY.route) {
                OrderSummaryScreen(
                    modifier = Modifier.fillMaxSize(),
                    orderUiState = uiState ?: OrderUiState(),
                    onSendButtonClicked = { subject, summary ->
                        // Отправка данных
                    }
                )
                currentScreen(Route.SUMMARY)
            }
        }
    }
}

@Composable
fun StartOrderScreen(
    modifier: Modifier = Modifier,
    quantityOptions: List<Pair<Int, Int>>,
    onQuantitySelected: (Int) -> Unit
) {
    // Реализация экрана выбора количества
}

@Composable
fun SelectOptionScreen(
    modifier: Modifier = Modifier,
    options: List<Int>,
    subtotal: String,
    onSelectionChanged: (String) -> Unit
) {
    // Реализация экрана выбора вкуса
}

@Composable
fun OrderSummaryScreen(
    modifier: Modifier = Modifier,
    orderUiState: OrderUiState,
    onSendButtonClicked: (String, String) -> Unit
) {
    // Реализация экрана подтверждения заказа
}

enum class Route {
    START,
    FLAVOR,
    SUMMARY
}
