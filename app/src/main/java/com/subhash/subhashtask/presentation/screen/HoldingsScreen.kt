package com.subhash.subhashtask.presentation.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.subhash.subhashtask.domain.model.Holding
import com.subhash.subhashtask.domain.model.PortfolioSummary
import com.subhash.subhashtask.presentation.viewmodel.HoldingsViewModel
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HoldingsScreen(
    modifier: Modifier = Modifier,
    viewModel: HoldingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val currencyFormatter = remember { DecimalFormat("₹#,##,##0.00") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        TopAppBar(
            title = {
                Text(
                    "Portfolio",
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
            },
            actions = {
                IconButton(onClick = { viewModel.loadHoldings(hardRefresh = true) }) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        tint = Color.White
                    )
                }
                IconButton(onClick = { }) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color.White
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFF1565C0)
            )
        )

        TabRow(
            selectedTabIndex = 1,
            containerColor = Color.White,
            contentColor = Color.Black,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier.tabIndicatorOffset(tabPositions[1]),
                    color = Color(0xFF1565C0),
                    height = 3.dp
                )
            }
        ) {
            Tab(
                selected = false,
                onClick = { },
                text = {
                    Text(
                        "POSITIONS",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray
                    )
                }
            )
            Tab(
                selected = true,
                onClick = { },
                text = {
                    Text(
                        "HOLDINGS",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                }
            )
        }

        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF1565C0))
                }
            }

            uiState.error != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Error: ${uiState.error}",
                        color = Color.Red,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.loadHoldings(hardRefresh = true) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1565C0)
                        )
                    ) {
                        Text("Retry", color = Color.White)
                    }
                }
            }

            else -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentPadding = PaddingValues(0.dp),
                        verticalArrangement = Arrangement.spacedBy(0.dp)
                    ) {
                        items(uiState.holdings) { holding ->
                            HoldingItem(
                                holding = holding,
                                currencyFormatter = currencyFormatter
                            )
                        }
                    }

                    uiState.portfolioSummary?.let { summary ->
                        PortfolioSummaryCard(
                            summary = summary,
                            isExpanded = uiState.isSummaryExpanded,
                            onToggle = { viewModel.toggleSummaryExpanded() },
                            currencyFormatter = currencyFormatter,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HoldingItem(
    holding: Holding,
    currencyFormatter: DecimalFormat
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = holding.symbol,
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = buildAnnotatedString {
                        append("NET QTY: ")
                        withStyle(style = SpanStyle(fontSize = 18.sp)) {
                            append("${holding.quantity}")
                        }
                    },
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                )

            }

            Column(horizontalAlignment = Alignment.End) {

                Text(
                    text = buildAnnotatedString {
                        append("LTP: ")
                        withStyle(style = SpanStyle(fontSize = 18.sp)) {
                            append("${currencyFormatter.format(holding.ltp)}")
                        }
                    },
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = buildAnnotatedString {
                        append("P&L: ")
                        withStyle(style = SpanStyle(fontSize = 18.sp)) {
                            append("${currencyFormatter.format(holding.pnl)}")
                        }
                    },
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = if (holding.pnl >= 0) Color(0xFF4CAF50) else Color(0xFFF44336)
                    )
                )

            }
        }

        Divider(
            color = Color.Gray.copy(alpha = 0.2f),
            thickness = 1.dp
        )
    }
}


@Composable
fun PortfolioSummaryCard(
    summary: PortfolioSummary,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    currencyFormatter: DecimalFormat,
    modifier: Modifier = Modifier,
) {
    val rotationAngle by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        label = "rotation"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFeeeeee)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column {
                    SummaryRow(
                        label = "Current value*",
                        value = currencyFormatter.format(summary.currentValue),
                        valueColor = Color.Black
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    SummaryRow(
                        label = "Total investment*",
                        value = currencyFormatter.format(summary.totalInvestment),
                        valueColor = Color.Black
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    SummaryRow(
                        label = "Today's Profit & Loss*",
                        value = currencyFormatter.format(summary.todaysPnl),
                        valueColor = if (summary.todaysPnl >= 0) Color(0xFF4CAF50) else Color(
                            0xFFE53935
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Divider(
                        color = Color(0xFFE0E0E0),
                        thickness = 1.dp
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggle() },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Profit & Loss*",
                        style = TextStyle(
                            fontSize = 16.sp,
                            color = Color(0xFF757575)
                        )
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        modifier = Modifier
                            .size(20.dp)
                            .rotate(rotationAngle),
                        tint = Color(0xFF757575)
                    )
                }

                val pnlPercentage = if (summary.totalInvestment != 0.0) {
                    (summary.totalPnl / summary.totalInvestment * 100)
                } else 0.0

                Text(
                    text = "${currencyFormatter.format(summary.totalPnl)} (${
                        String.format(
                            "%.2f",
                            pnlPercentage
                        )
                    }%)",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        color = if (summary.totalPnl >= 0) Color(0xFF4CAF50) else Color(0xFFE53935)
                    )
                )
            }
        }
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
    valueColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = TextStyle(
                fontSize = 14.sp,
                color = Color.Gray
            )
        )
        Text(
            text = value,
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = valueColor
            )
        )
    }
}
