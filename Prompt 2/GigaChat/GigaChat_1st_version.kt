6.	@Composable//GigaChat prompt 2 1st attempt
7.	fun EveryUserReview(message: Message, expanded: MutableState<Boolean>) {
8.	    Column(
9.	        verticalArrangement = Arrangement.Top,
10.	        horizontalAlignment = Alignment.Start,
11.	        modifier = Modifier
12.	            .padding(horizontal = 16.dp, vertical = 8.dp)
13.	    ) {
14.	        Row(
15.	            verticalAlignment = Alignment.CenterVertically
16.	        ) {
17.	            Image(
18.	                painter = painterResource(id = message.uLogo),
19.	                contentDescription = null,
20.	                modifier = Modifier
21.	                    .size(50.dp)
22.	                    .border(
23.	                        width = 1.dp,
24.	                        color = MaterialTheme.colorScheme.primary,
25.	                        shape = CircleShape
26.	                    )
27.	                    .clip(CircleShape)
28.	            )
29.	            
30.	            Spacer(modifier = Modifier.width(16.dp))
31.	            
32.	            Column {
33.	                Text(
34.	                    text = message.author,
35.	                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
36.	                    color = MaterialTheme.colorScheme.onBackground
37.	                )
38.	                
39.	                Text(
40.	                    text = message.time,
41.	                    style = MaterialTheme.typography.bodySmall,
42.	                    color = MaterialTheme.colorScheme.secondary
43.	                )
44.	            }
45.	        }
46.	        
47.	        Spacer(modifier = Modifier.height(8.dp))
48.	        
49.	        Surface(
50.	            shape = MaterialTheme.shapes.medium,
51.	            color = if(expanded.value) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
52.	            modifier = Modifier.clickable(onClick = { expanded.value = !expanded.value })
53.	        ) {
54.	            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onBackground) {
55.	                Text(
56.	                    text = message.body,
57.	                    style = MaterialTheme.typography.bodySmall,
58.	                    maxLines = if(expanded.value) Int.MAX_VALUE else 2,
59.	                    overflow = TextOverflow.Ellipsis,
60.	                    modifier = Modifier.padding(all = 16.dp)
61.	                )
62.	            }
63.	        }
64.	    }
65.	}
66.	
67.	@Composable
68.	fun MainScreenElements(messages: List<Message>) {
69.	    val context = LocalContext.current
70.	    
71.	    LazyColumn(
72.	        modifier = Modifier
73.	            .fillMaxWidth()
74.	    ) {
75.	        items(messages) { message ->
76.	            var expanded by remember { mutableStateOf(false) }
77.	            EveryUserReview(message, expanded)
78.	        }
79.	    }
80.	}