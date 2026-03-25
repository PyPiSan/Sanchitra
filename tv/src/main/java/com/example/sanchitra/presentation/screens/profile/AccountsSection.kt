package com.example.sanchitra.presentation.screens.profile

import AccountsSelectionItem
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import com.example.sanchitra.data.util.StringConstants
import com.example.sanchitra.presentation.screens.dashboard.rememberChildPadding

@Immutable
data class AccountsSectionData(
    val title: String,
    val value: String? = null,
    val onClick: () -> Unit = {}
)

@Composable
fun AccountsSection() {
    val childPadding = rememberChildPadding()
    var showDeleteDialog by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val accountsSectionListItems = remember {
        listOf(
            AccountsSectionData(
                title = StringConstants.Composable.Placeholders
                    .AccountsSelectionSwitchAccountsTitle,
                value = StringConstants.Composable.Placeholders.AccountsSelectionSwitchAccountsEmail
            ),
            AccountsSectionData(
                title = StringConstants.Composable.Placeholders.AccountsSelectionLogOut,
                value = StringConstants.Composable.Placeholders.AccountsSelectionSwitchAccountsEmail
            ),
            AccountsSectionData(
                title = StringConstants.Composable.Placeholders
                    .AccountsSelectionChangePasswordTitle,
                value = StringConstants.Composable.Placeholders.AccountsSelectionChangePasswordValue
            ),
            AccountsSectionData(
                title = StringConstants.Composable.Placeholders.AccountsSelectionAddNewAccountTitle,
            ),
            AccountsSectionData(
                title = StringConstants.Composable.Placeholders
                    .AccountsSelectionViewSubscriptionsTitle
            ),
            AccountsSectionData(
                title = StringConstants.Composable.Placeholders.AccountsSelectionDeleteAccountTitle,
                onClick = { showDeleteDialog = true }
            )
        )
    }

    LazyVerticalGrid(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = childPadding.start),
        columns = GridCells.Fixed(2),
        content = {
            items(accountsSectionListItems.size) { index ->
                AccountsSelectionItem(
                    modifier = Modifier.focusRequester(focusRequester),
                    key = index,
                    accountsSectionData = accountsSectionListItems[index]
                )
            }
        }
    )

    AccountsSectionDeleteDialog(
        showDialog = showDeleteDialog,
        onDismissRequest = { showDeleteDialog = false },
        modifier = Modifier.width(428.dp)
    )
}
