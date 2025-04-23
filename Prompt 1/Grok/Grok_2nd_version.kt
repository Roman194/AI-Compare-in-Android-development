/**
*Модифицируй пожалуйста предыдущее решение.  Реализованные в рамках навигации Composable-функции должны иметь ряд других параметров. Для StartOrderScreen это параметр onNextButtonClicked типа 
* (Int) -> Unit вместо onSelectionChanged. Для  SelectOptionScreen нужно добавить onNextButtonClicked типа () -> Unit = {}. Для OrderSummaryScreen -  onSendButtonClicked типа (String, String) -> Unit вместо onConfirmButtonClicked.

*Обрати также внимание на то, что в onSendButtonClicked ожидается 2 параметра типа String, а не 1. Первый с названием subject, а второй - summary. Обрати внимание на параметры quantityOptions и options! Они используют данные, полученные из переменной uiState или объекта DataSource. 

*Ниже прикрепляю объект DataSorce и data class OrderUiState, которые должны помочь тебе доработать код

*object DataSource {    val flavors = listOf(        R.string.vanilla,        R.string.chocolate,        R.string.red_velvet,        R.string.salted_caramel,        R.string.coffee    )


*val quantityOptions = listOf(
*    Pair(R.string.one_cupcake, 1),
*    Pair(R.string.six_cupcakes, 6),
*    Pair(R.string.twelve_cupcakes, 12)
*)
*}

/**
 * Data class that represents the current UI state in terms of [quantity], [flavor],
 * [dateOptions], selected pickup [date] and [price]
 */
*data class OrderUiState(
    /** Selected cupcake quantity (1, 6, 12) */
*    val quantity: Int = 0,
    /** Flavor of the cupcakes in the order (such as "Chocolate", "Vanilla", etc..) */
*    val flavor: String = "",
    /** Selected date for pickup (such as "Jan 1") */
*    val date: String = "",
    /** Total price for the order */
*    val price: String = "",
    /** Available pickup dates for the order*/
*    val pickupOptions: List<String> = listOf()
*)
*/
enum class CupcakeScreen(@StringRes val title: Int) {
    Start(title = R.string.app_name),
    Flavor(title = R.string.flavor),
    Pickup(title = R.string.choose_pickup_date),
    Summary(title = R.string.order_summary)
}

@Composable //Grok prompt 1 2nd attempt
fun CupcakeAppBar(
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    currentScreen: CupcakeScreen,
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

@Composable
fun CupcakeApp(
    viewModel: OrderViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    Scaffold(
        topBar = {
            CupcakeAppBar(
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() },
                currentScreen = navController.currentBackStackEntryAsState().value?.destination?.route?.let {
                    CupcakeScreen.valueOf(it)
                } ?: CupcakeScreen.Start
            )
        }
    ) { innerPadding ->
        val uiState by viewModel.uiState.collectAsState()

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
                    subtotal = uiState.price,
                    options = DataSource.flavors.map { stringResource(id = it) },
                    onSelectionChanged = { flavor ->
                        viewModel.setFlavor(flavor)
                    },
                    onNextButtonClicked = {
                        navController.navigate(CupcakeScreen.Pickup.name)
                    },
                    onCancelButtonClicked = { cancelOrderAndNavigateToStart(viewModel, navController) },
                    modifier = Modifier.fillMaxSize()
                )
            }
            composable(CupcakeScreen.Pickup.name) {
                SelectOptionScreen(
                    subtotal = uiState.price,
                    options = uiState.pickupOptions,
                    onSelectionChanged = { date ->
                        viewModel.setDate(date)
                    },
                    onNextButtonClicked = {
                        navController.navigate(CupcakeScreen.Summary.name)
                    },
                    onCancelButtonClicked = { cancelOrderAndNavigateToStart(viewModel, navController) },
                    modifier = Modifier.fillMaxSize()
                )
            }
            composable(CupcakeScreen.Summary.name) {
                OrderSummaryScreen(
                    orderUiState = uiState,
                    onSendButtonClicked = { subject, summary ->
                        // TODO: Implement order confirmation
                    },
                    onCancelButtonClicked = { cancelOrderAndNavigateToStart(viewModel, navController) },
                    modifier = Modifier.fillMaxSize()
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
