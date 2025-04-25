// !The running version of the code!
/**
*Модифицируй пожалуйста предыдущее решение.  Реализованным в рамках навигации Composable-функциям не хватает ряда параметров. Функция StartOrderScreen должна иметь параметр quatityOptions типа List<Pair<Int, Int>> и modifier типа Modifier, функциям SelectOptionScreen, OrderSummaryScreen и CupcakeAppBar также не хватает этого modifier. 

*Обрати также внимание на то, что в onSendButtonClicked ожидается 2 параметра типа String, а не 1. Первый с названием subject, а второй - summary.  В изначальном шаблоне была переменная uiState. Обрати на неё внимание! Она не лишняя и используется в параметрах options (за исключением SelectOptionScreen где route flavour, там используются данные из DataSource, что ты уже сделал и это менять не нужно), subtotal и orderUiState.  

*Ниже прикрепляю объект DataSorce и data class OrderUiState, которые должны помочь тебе доработать код. 

*Также обрати внимание на переменную currentScreen. Она нужна для изменения заголовка в функции cupcakeAppBar. Для этого этот параметр необходимо передать в эту функцию.

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

@Composable//ChatGPT prompt 1 2nd attempt
fun CupcakeApp(
    orderViewModel: OrderViewModel = viewModel()
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = CupcakeScreen.valueOf(
        backStackEntry?.destination?.route ?: CupcakeScreen.Start.name
    )
    val uiState = orderViewModel.uiState.collectAsState().value

    Scaffold(
        topBar = {
            CupcakeAppBar(
                currentScreen = currentScreen,
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
            composable(route = CupcakeScreen.Start.name) {
                StartOrderScreen(
                    quantityOptions = DataSource.quantityOptions,
                    onNextButtonClicked = {
                        orderViewModel.setQuantity(it)
                        navController.navigate(CupcakeScreen.Flavor.name)
                    },
                    modifier = Modifier
                )
            }
            composable(route = CupcakeScreen.Flavor.name) {
                SelectOptionScreen(
                    subtotal = uiState.price,
                    options = DataSource.flavors.map { id -> stringResource(id) },
                    onSelectionChanged = { orderViewModel.setFlavor(it) },
                    onNextButtonClicked = { navController.navigate(CupcakeScreen.Pickup.name) },
                    onCancelButtonClicked = {
                        cancelOrderAndNavigateToStart(orderViewModel, navController)
                    },
                    modifier = Modifier
                )
            }
            composable(route = CupcakeScreen.Pickup.name) {
                SelectOptionScreen(
                    subtotal = uiState.price,
                    options = uiState.pickupOptions,
                    onSelectionChanged = { orderViewModel.setDate(it) },
                    onNextButtonClicked = { navController.navigate(CupcakeScreen.Summary.name) },
                    onCancelButtonClicked = {
                        cancelOrderAndNavigateToStart(orderViewModel, navController)
                    },
                    modifier = Modifier
                )
            }
            composable(route = CupcakeScreen.Summary.name) {
                OrderSummaryScreen(
                    orderUiState = uiState,
                    onCancelButtonClicked = {
                        cancelOrderAndNavigateToStart(orderViewModel, navController)
                    },
                    onSendButtonClicked = { subject: String, summary: String ->
                        // TODO: Реализовать отправку заказа с использованием subject и summary
                    },
                    modifier = Modifier
                )
            }
        }
    }
}


private fun cancelOrderAndNavigateToStart(
    viewModel: OrderViewModel,
    navController: NavHostController
) {
    viewModel.resetOrder()
    navController.popBackStack(CupcakeScreen.Start.name, inclusive = false)
}

@Composable
fun CupcakeAppBar(
    currentScreen: CupcakeScreen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(stringResource(id = currentScreen.title)) },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        }
    )
}
