// !The running version of the code!
/**

* Отлично, программа запустилась! Но функция cancelOrderAndNavigateToStart всё ещё не private. Для того, чтобы она стала private, нужно её пометить одноимённым модификатором. Возможно стоит 
* вынести её за пределы функции CupcakeApp для того, чтобы это стало возможным.
*/

@Composable //perplexity prompt 1 3rd attempt
fun CupcakeApp(
    viewModel: OrderViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    val uiState by viewModel.uiState.collectAsState()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = when (backStackEntry?.destination?.route) {
        CupcakeScreen.Start.name -> CupcakeScreen.Start
        CupcakeScreen.Flavor.name -> CupcakeScreen.Flavor
        CupcakeScreen.Pickup.name -> CupcakeScreen.Pickup
        CupcakeScreen.Summary.name -> CupcakeScreen.Summary
        else -> CupcakeScreen.Start
    }

    val canNavigateBack = navController.previousBackStackEntry != null

    Scaffold(
        topBar = {
            CupcakeAppBar(
                canNavigateBack = canNavigateBack,
                navigateUp = { navController.navigateUp() },
                title = currentScreen.title
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
                    modifier = Modifier.fillMaxSize(),
                    onNextButtonClicked = { quantity ->
                        viewModel.setQuantity(quantity)
                        navController.navigate(CupcakeScreen.Flavor.name)
                    }
                )
            }
            composable(route = CupcakeScreen.Flavor.name) {
                SelectOptionScreen(
                    options = DataSource.flavors.map { stringResource(it) },
                    subtotal = uiState.price,
                    modifier = Modifier.fillMaxSize(),
                    onSelectionChanged = { flavor ->
                        viewModel.setFlavor(flavor)
                    },
                    onNextButtonClicked = {
                        navController.navigate(CupcakeScreen.Pickup.name)
                    },
                    onCancelButtonClicked = { cancelOrderAndNavigateToStart(viewModel, navController) }
                )
            }
            composable(route = CupcakeScreen.Pickup.name) {
                SelectOptionScreen(
                    options = uiState.pickupOptions,
                    subtotal = uiState.price,
                    modifier = Modifier.fillMaxSize(),
                    onSelectionChanged = { date ->
                        viewModel.setDate(date)
                    },
                    onNextButtonClicked = {
                        navController.navigate(CupcakeScreen.Summary.name)
                    },
                    onCancelButtonClicked = { cancelOrderAndNavigateToStart(viewModel, navController) }
                )
            }
            composable(route = CupcakeScreen.Summary.name) {
                OrderSummaryScreen(
                    orderUiState = uiState,
                    modifier = Modifier.fillMaxSize(),
                    onCancelButtonClicked = { cancelOrderAndNavigateToStart(viewModel, navController) },
                    onSendButtonClicked = { subject: String, summary: String ->
                        // TODO: Реализовать подтверждение заказа
                    }
                )
            }
        }
    }
}

// Вынесенная и private функция для отмены заказа и возврата на стартовый экран
private fun cancelOrderAndNavigateToStart(
    viewModel: OrderViewModel,
    navController: NavHostController
) {
    viewModel.resetOrder()
    navController.popBackStack(CupcakeScreen.Start.name, inclusive = false)
}
