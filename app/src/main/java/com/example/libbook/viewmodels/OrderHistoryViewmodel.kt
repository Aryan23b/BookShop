package com.example.libbook.viewmodels


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.libbook.data.local.BookShopDatabase
import com.example.libbook.data.models.Order
import com.example.libbook.data.models.OrderDetailWithBook
import com.example.libbook.data.remote.RetrofitInstance
import com.example.libbook.repository.BookShopRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class OrderHistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: BookShopRepository

    // Holds the list of all orders for a user
    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders

    // Holds the details for a single, selected order
    private val _selectedOrderDetails = MutableStateFlow<List<OrderDetailWithBook>>(emptyList())
    val selectedOrderDetails: StateFlow<List<OrderDetailWithBook>> = _selectedOrderDetails

    private val _selectedOrder = MutableStateFlow<Order?>(null)
    val selectedOrder: StateFlow<Order?> = _selectedOrder


    init {
        val bookShopDao = BookShopDatabase.getDatabase(application).bookShopDao()
        repository = BookShopRepository(bookShopDao, RetrofitInstance.api)
    }

    fun loadUserOrders(username: String) {
        viewModelScope.launch {
            repository.getOrdersForUser(username).collect { orderList ->
                _orders.value = orderList
            }
        }
    }

//    fun loadOrderDetails(orderId: Long) {
//        viewModelScope.launch {
//            repository.getDetailedOrderInfo(orderId).collect { detailsList ->
//                _selectedOrderDetails.value = detailsList
//            }
//        }
//    }

    fun loadOrderDetails(orderId: Long) {
        viewModelScope.launch {
            // Also fetch the parent order object
            _selectedOrder.value = repository.getOrderById(orderId)
            repository.getDetailedOrderInfo(orderId).collect { detailsList ->
                _selectedOrderDetails.value = detailsList
            }
        }
    }
}