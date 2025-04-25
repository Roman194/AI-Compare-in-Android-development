// !The running version of the code!
/**
* Модифицируй пожалуйста предыдущее решение.  Реализованным в рамках навигации Composable-функциям не хватает ряда параметров. Функция StartOrderScreen должна иметь параметры quatityOptions 
* типа List<Pair<Int, Int>> и modifier, функция SelectOptionScreen - параметры subtotal типа  String, onNextButtonClicked типа unit (здесь должна происходить навигация на следующий экран, а 
* не в onSelectionChanged) и modifier, а также не должно быть параметра title. Функция OrderSummaryScreen должна иметь параметр onSendButtonClicked типа (String, String) -> Unit вместо 
* onConfirmButtonClicked, где ожидаются параметры с названиями subject и summary.

* Обрати также внимание на то, что в onSendButtonClicked ожидается 2 параметра типа String, а не 1. Первый с названием subject, а второй - summary. Обрати внимание на переменную uiState! Она 
* должна использоваться в параметрах options (за исключением SelectOptionScreen где route flavour, там используются данные из DataSource), subtotal и orderUiState. Причём в параметре options 
* должны приходить данные типа List<String>

* Ниже прикрепляю объект DataSorce и data class OrderUiState, которые должны помочь тебе доработать код.
*/

@Composable //Qwen prompt 1 2nd attempt
fun CupcakeApp(
    viewModel: OrderViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    // Состояние заказа
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            CupcakeAppBar(
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() }
            )
        }
    ) { innerPadding ->
        // NavHost для управления навигацией
        NavHost(
            navController = navController,
            startDestination = CupcakeScreen.Start.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(CupcakeScreen.Start.name) {
                StartOrderScreen(
                    quantityOptions = DataSource.quantityOptions,
                    onNextButtonClicked = { quantity ->
                        viewModel.setQuantity(quantity)
                        navController.navigate(CupcakeScreen.Flavor.name)
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }

            composable(CupcakeScreen.Flavor.name) {
                SelectOptionScreen(
                    options = DataSource.flavors.map { stringResource(it) },
                    subtotal = uiState.price,
                    onNextButtonClicked = {
                        navController.navigate(CupcakeScreen.Pickup.name)
                    },
                    onCancelButtonClicked = { cancelOrderAndNavigateToStart(navController, viewModel) },
                    modifier = Modifier.fillMaxSize()
                )
            }

            composable(CupcakeScreen.Pickup.name) {
                SelectOptionScreen(
                    options = uiState.pickupOptions,
                    subtotal = uiState.price,
                    onNextButtonClicked = {
                        navController.navigate(CupcakeScreen.Summary.name)
                    },
                    onCancelButtonClicked = { cancelOrderAndNavigateToStart(navController, viewModel) },
                    modifier = Modifier.fillMaxSize()
                )
            }

            composable(CupcakeScreen.Summary.name) {
                OrderSummaryScreen(
                    orderUiState = uiState,
                    onSendButtonClicked = { subject, summary ->
                        TODO("Implement sending logic with subject and summary")
                    },
                    onCancelButtonClicked = { cancelOrderAndNavigateToStart(navController, viewModel) }
                )
            }
        }
    }
}

// Функция для отмены заказа и возврата на стартовый экран
private fun cancelOrderAndNavigateToStart(
    navController: NavHostController,
    viewModel: OrderViewModel
) {
    viewModel.resetOrder()
    navController.popBackStack(CupcakeScreen.Start.name, inclusive = false)
}
