/**
* Модифицируй пожалуйста предыдущее решение.  Функция OrderSummaryScreen должна иметь параметр onSendButtonClicked типа (String, String) -> Unit вместо onNextButtonClicked, где ожидаются 
*параметры с названиями subject и summary. Функция SelectedOptionScreen имеет лишний параметр selectedOption. Его необходимо убрать. Не забудь добавить во всех функциях внутри NavHost параметр
* modifier. 

* В функции CupcakeApp не хватает переменных backStackEntry и currentScreen. Последняя в зависимости от текущего элемента из enum class-а меняет значение тайтла в CupcakeAppBar и поэтому 
* должна быть передана в эту функцию

* Также необходимо переписать названия методов, которые используются в uiState.

* Ниже прикрепляю объект DataSorce и data class OrderUiState, которые должны помочь тебе доработать код

* object DataSource {
*     val flavors = listOf(
*         R.string.vanilla,
*         R.string.chocolate,
*         R.string.red_velvet,
*        R.string.salted_caramel,
*        R.string.coffee
 *   )

*    val quantityOptions = listOf(
 *       Pair(R.string.one_cupcake, 1),
  *      Pair(R.string.six_cupcakes, 6),
   *     Pair(R.string.twelve_cupcakes, 12)
    *)
*}

/**
 * Data class that represents the current UI state in terms of [quantity], [flavor],
 * [dateOptions], selected pickup [date] and [price]
 */
*data class OrderUiState(
*    /** Selected cupcake quantity (1, 6, 12) */
*    val quantity: Int = 0,
*    /** Flavor of the cupcakes in the order (such as "Chocolate", "Vanilla", etc..) */
*    val flavor: String = "",
*    /** Selected date for pickup (such as "Jan 1") */
*    val date: String = "",
*    /** Total price for the order */
*    val price: String = "",
*    /** Available pickup dates for the order*/
*    val pickupOptions: List<String> = listOf()
*)
*/

@Composable // Mistral prompt 1 2nd attempt
fun CupcakeApp(
    viewModel: OrderViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = CupcakeScreen.valueOf(
        backStackEntry?.destination?.route ?: CupcakeScreen.Start.name
    )

    Scaffold(
        topBar = {
            CupcakeAppBar(
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() },
                currentScreen = currentScreen
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
                    onNextButtonClicked = {
                        navController.navigate(CupcakeScreen.Flavor.name)
                    },
                    modifier = Modifier
                )
            }
            composable(CupcakeScreen.Flavor.name) {
                SelectOptionScreen(
                    subtotal = uiState.price,
                    onNextButtonClicked = {
                        navController.navigate(CupcakeScreen.Pickup.name)
                    },
                    onCancelButtonClicked = {
                        cancelOrderAndNavigateToStart(viewModel, navController)
                    },
                    options = DataSource.flavors,
                    onSelectionChanged = { viewModel.setFlavor(it) },
                    modifier = Modifier
                )
            }
            composable(CupcakeScreen.Pickup.name) {
                SelectOptionScreen(
                    subtotal = uiState.price,
                    onNextButtonClicked = {
                        navController.navigate(CupcakeScreen.Summary.name)
                    },
                    onCancelButtonClicked = {
                        cancelOrderAndNavigateToStart(viewModel, navController)
                    },
                    options = uiState.pickupOptions,
                    onSelectionChanged = { viewModel.setDate(it) },
                    modifier = Modifier
                )
            }
            composable(CupcakeScreen.Summary.name) {
                OrderSummaryScreen(
                    orderUiState = uiState,
                    onSendButtonClicked = { subject, summary ->
                        // TODO: implement order confirmation
                    },
                    onCancelButtonClicked = {
                        cancelOrderAndNavigateToStart(viewModel, navController)
                    },
                    modifier = Modifier
                )
            }
        }
    }
}

@Composable
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

private fun cancelOrderAndNavigateToStart(
    viewModel: OrderViewModel,
    navController: NavHostController
) {
    viewModel.resetOrder()
    navController.popBackStack(CupcakeScreen.Start.name, inclusive = false)
}
