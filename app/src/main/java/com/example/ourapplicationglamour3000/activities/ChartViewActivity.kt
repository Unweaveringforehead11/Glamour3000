package com.example.ourapplicationglamour3000.activities

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.ourapplicationglamour3000.databinding.ActivityChartViewBinding
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase

class ChartViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChartViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChartViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadItemsCount()

        binding.backBtn.setOnClickListener {
            val intent= Intent(this, DashboardUserActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun loadItemsCount() {
        val chart: BarChart = binding.chart
        val values1 = mutableListOf<Float>()
        val values2 = mutableListOf<Float>()
        val categoryNames = mutableListOf<String>()

        val ref = FirebaseDatabase.getInstance().getReference("Categories")
        val categoryGoalQuery = ref.orderByChild("categoryGoal")

        categoryGoalQuery.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val goal = "${snapshot.child("categoryGoal").value}"
                    val categoryName = "${snapshot.child("categoryTitle").value}"
                    val categoryId = snapshot.key // Get the category ID
                    categoryNames.add(categoryName)

                    // Count the number of items in the category
                    val ref1 = FirebaseDatabase.getInstance().getReference("Items")
                    val itemsQuery = ref1.orderByChild("itemCategoryId").equalTo(categoryId)

                    itemsQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(itemSnapshot: DataSnapshot) {
                            val itemCount = itemSnapshot.childrenCount
                            values2.add(itemCount.toFloat())

                            // Check if the number of items matches the number of categories
                            if (values2.size == categoryNames.size) {
                                // Once you have fetched the data, you can create the chart here
                                createChart(chart, values1, values2, categoryNames)
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            // Handle the error
                        }
                    })

                    values1.add(goal.toFloat())
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle the error
            }
        })
    }


    fun createChart(chart: BarChart, values1: List<Float>, values2: List<Float>, categoryNames: List<String>) {
        val entries = mutableListOf<BarEntry>()

        for (i in values1.indices) {
            val value1 = values1[i]
            val value2 = values2[i]
            entries.add(BarEntry(i.toFloat(), floatArrayOf(value1, value2)))
        }

        val dataSet = BarDataSet(entries, " ")

        // Customize the appearance of the dataset
        dataSet.colors = listOf(Color.BLUE, Color.RED)
        dataSet.stackLabels = arrayOf("Category Goal", "Added Items")

        val barData = BarData(dataSet)
        barData.barWidth = 0.4f

        // Customize the appearance of the chart
        chart.description.isEnabled = false
        chart.legend.isEnabled = true
        chart.setDrawGridBackground(false)
        chart.setDrawBarShadow(false)
        chart.setPinchZoom(false)

        // Set the X-axis labels
        val xAxis = chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(true)
        xAxis.valueFormatter = IndexAxisValueFormatter(categoryNames) // Set category names as X-axis labels
        xAxis.granularity = 1f // Ensure only one label is shown per index


        // Set the Y-axis labels
        val yAxis = chart.axisLeft
        yAxis.setDrawGridLines(true)

        chart.data = barData
        chart.animateY(1000)
        chart.invalidate() // Refresh the chart
    }

}