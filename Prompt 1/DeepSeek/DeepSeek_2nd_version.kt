// !The running version of the code!
/**
*Модифицируй пожалуйста предыдущее решение.  Реализованным в рамках навигации Composable-функциям не хватает ряда параметров. Функция StartOrderScreen должна иметь параметр quatityOptions типа List<Pair<Int, Int>>, функция SelectOptionScreen - параметр subtotal типа  String, а для функции OrderSummaryScreen - параметра orderUiState типа OrderUiState.

*Обрати также внимание на то, что в onSendButtonClicked ожидается 2 параметра типа String, а не 1. Первый с названием subject, а второй - summary.  В изначальном шаблоне была переменная uiState. Обрати на неё внимание! Она не лишняя и используется в параметрах options (за исключением SelectOptionScreen где route flavour, там используются данные из DataSource), subtotal и orderUiState. 

*Ниже прикрепляю объект DataSorce и data class OrderUiState, которые должны помочь тебе доработать код

object DataSource {
    val flavors = listOf(
        R.string.vanilla,
        R.string.chocolate,
        R.string.red_velvet,
        R.string.salted_caramel,
        R.string.coffee
    )

    val quantityOptions = listOf(
        Pair(R.string.one_cupcake, 1),
        Pair(R.string.six_cupcakes, 6),
        Pair(R.string.twelve_cupcakes, 12)
    )
}

/**
 * Data class that represents the current UI state in terms of [quantity], [flavor],
 * [dateOptions], selected pickup [date] and [price]
 */
data class OrderUiState(
    /** Selected cupcake quantity (1, 6, 12) */
    val quantity: Int = 0,
    /** Flavor of the cupcakes in the order (such as "Chocolate", "Vanilla", etc..) */
    val flavor: String = "",
    /** Selected date for pickup (such as "Jan 1") */
    val date: String = "",
    /** Total price for the order */
    val price: String = "",
    /** Available pickup dates for the order*/
    val pickupOptions: List<String> = listOf()
)
*/

@Composable //DeepSeek prompt 1 2nd version
fun CupcakeApp(
    viewModel: OrderViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = CupcakeScreen.valueOf(
        backStackEntry?.destination?.route ?: CupcakeScreen.Start.name
    )
    val uiState by viewModel.uiState.collectAsState()

    fun cancelOrderAndNavigateToStart() {
        viewModel.resetOrder()
        navController.popBackStack(CupcakeScreen.Start.name, inclusive = false)
    }

    Scaffold(
        topBar = {
            CupcakeAppBar(
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = CupcakeScreen.Start.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Стартовый экран
            composable(route = CupcakeScreen.Start.name) {
                StartOrderScreen(
                    quantityOptions = DataSource.quantityOptions,
                    onNextButtonClicked = { quantity ->
                        viewModel.setQuantity(quantity)
                        navController.navigate(CupcakeScreen.Flavor.name)
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
            
            // Экран выбора вкуса
            composable(route = CupcakeScreen.Flavor.name) {
                SelectOptionScreen(
                    options = DataSource.flavors.map { stringResource(it) },
                    subtotal = uiState.price,
                    onSelectionChanged = { viewModel.setFlavor(it) },
                    onNextButtonClicked = { 
                        navController.navigate(CupcakeScreen.Pickup.name) 
                    },
                    onCancelButtonClicked = { cancelOrderAndNavigateToStart() },
                    modifier = Modifier.fillMaxSize()
                )
            }
            
            // Экран выбора даты
            composable(route = CupcakeScreen.Pickup.name) {
                SelectOptionScreen(
                    options = uiState.pickupOptions,
                    subtotal = uiState.price,
                    onSelectionChanged = { viewModel.setDate(it) },
                    onNextButtonClicked = { 
                        navController.navigate(CupcakeScreen.Summary.name) 
                    },
                    onCancelButtonClicked = { cancelOrderAndNavigateToStart() },
                    modifier = Modifier.fillMaxSize()
                )
            }
            
            // Экран подтверждения заказа
            composable(route = CupcakeScreen.Summary.name) {
                OrderSummaryScreen(
                    orderUiState = uiState,
                    onCancelButtonClicked = { cancelOrderAndNavigateToStart() },
                    onSendButtonClicked = { subject, summary ->
                        // TODO: Implement send order logic
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
