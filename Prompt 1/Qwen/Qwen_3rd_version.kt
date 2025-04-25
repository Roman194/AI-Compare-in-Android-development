// !The running version of the code!
/**
* Необходимо ещё доработать код. В функции SelectOptionScreen не хватает параметра onSelectionChanged, где с помощью соответствующих функций из viewModel задавались бы значения для flavour и 
* date. 

* Также в функции CupcakeApp не хватает переменных backStackEntry и currentScreen. Последняя в зависимости от текущего элемента из enum class-а меняет значение тайтла в CupcakeAppBar и поэтому 
* должна быть передана в эту функцию.
*/

@Composable //Qwen prompt 1 3rd attempt
fun CupcakeApp(
    viewModel: OrderViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    // Состояние заказа
    val uiState by viewModel.uiState.collectAsState()

    // Текущий элемент стека навигации
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = CupcakeScreen.valueOf(
        backStackEntry?.destination?.route ?: CupcakeScreen.Start.name
    )

    Scaffold(
        topBar = {
            CupcakeAppBar(
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() },
                title = currentScreen.title // Передаем текущий заголовок
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
                    onSelectionChanged = { flavor ->
                        viewModel.setFlavor(flavor) // Обновляем вкус
                    },
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
                    onSelectionChanged = { date ->
                        viewModel.setDate(date) // Обновляем дату
                    },
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

@Composable
fun CupcakeAppBar(
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    @StringRes title: Int,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(stringResource(id = title)) }, // Используем переданный заголовок
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
