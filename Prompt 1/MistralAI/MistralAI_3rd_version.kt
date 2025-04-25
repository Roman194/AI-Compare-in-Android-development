// !The running version of the code!
/**
* Уже почти! Осталось чуть-чуть доработать. В функции SelectOptionsScreen в параметре options DataSource.flavors предоставляет данные типа List<Int>, а требуются данные типа List<String>. 
* Поэтому эти данные необходимо конвертировать в тип String и передать в функцию в этом формате.
*/

@Composable // Mistral prompt 1 3rd attempt
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
                    options = DataSource.flavors.map { stringResource(id = it) },
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
