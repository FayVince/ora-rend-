package com.fayvince.orarend.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.*
import com.fayvince.orarend.viewmodel.ScheduleViewModel

@Composable
fun ScheduleScreen(
    viewModel: ScheduleViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    
    Scaffold(
        timeText = {
            TimeText()
        }
    ) {
        ScalingLazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            contentPadding = PaddingValues(
                top = 32.dp,
                start = 10.dp,
                end = 10.dp,
                bottom = 32.dp
            ),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when {
                state.isWeekend -> {
                    item {
                        Text(
                            text = "Hétvége",
                            style = MaterialTheme.typography.title2,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colors.primary
                        )
                    }
                    item {
                        Text(
                            text = "🎉",
                            style = MaterialTheme.typography.display1,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                
                state.currentLesson != null && state.currentPeriod != null -> {
                    // Current lesson is in progress
                    item {
                        Text(
                            text = "Jelenlegi óra",
                            style = MaterialTheme.typography.caption1,
                            color = Color.Gray
                        )
                    }
                    
                    item {
                        Text(
                            text = state.currentLesson!!.subject,
                            style = MaterialTheme.typography.title1,
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                            onClick = {}
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Terem: ${state.currentLesson!!.room}",
                                    style = MaterialTheme.typography.body2,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                    
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Hátra",
                            style = MaterialTheme.typography.caption1,
                            color = Color.Gray
                        )
                    }
                    
                    item {
                        Text(
                            text = viewModel.formatRemainingTime(state.remainingSeconds),
                            style = MaterialTheme.typography.display1,
                            color = MaterialTheme.colors.primary,
                            textAlign = TextAlign.Center
                        )
                    }
                    
                    // Show next lesson if available
                    if (state.nextLesson != null) {
                        item {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Következő",
                                style = MaterialTheme.typography.caption1,
                                color = Color.Gray
                            )
                        }
                        
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp),
                                onClick = {}
                            ) {
                                Column(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = state.nextLesson!!.subject,
                                        style = MaterialTheme.typography.body1,
                                        textAlign = TextAlign.Center,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = state.nextLesson!!.room,
                                        style = MaterialTheme.typography.caption2,
                                        textAlign = TextAlign.Center,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }
                
                state.isBreak && state.nextLesson != null -> {
                    // Break time
                    item {
                        Text(
                            text = "Szünet",
                            style = MaterialTheme.typography.title2,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colors.primary
                        )
                    }
                    
                    item {
                        Text(
                            text = "☕",
                            style = MaterialTheme.typography.display1,
                            textAlign = TextAlign.Center
                        )
                    }
                    
                    item {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Következő óra",
                            style = MaterialTheme.typography.caption1,
                            color = Color.Gray
                        )
                    }
                    
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                            onClick = {}
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = state.nextLesson!!.subject,
                                    style = MaterialTheme.typography.title3,
                                    textAlign = TextAlign.Center,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Terem: ${state.nextLesson!!.room}",
                                    style = MaterialTheme.typography.body2,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                if (state.nextPeriod != null) {
                                    Text(
                                        text = "${state.nextPeriod!!.startTime} - ${state.nextPeriod!!.endTime}",
                                        style = MaterialTheme.typography.caption2,
                                        color = Color.Gray,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
                
                else -> {
                    // No more lessons today or before school starts
                    item {
                        Text(
                            text = "Nincs több óra",
                            style = MaterialTheme.typography.title2,
                            textAlign = TextAlign.Center
                        )
                    }
                    
                    item {
                        Text(
                            text = "🏠",
                            style = MaterialTheme.typography.display1,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
