package com.example.quizgame.view

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.quizgame.R
import com.example.quizgame.databinding.FragmentResultBinding
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry


class FragmentResult : Fragment() {

    lateinit var fragmentresultBinding : FragmentResultBinding

    var correctNumber = 0F
    var emptyNumber = 0F
    var wrongNumber = 0F

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentresultBinding = FragmentResultBinding.inflate(inflater,container,false)

        val args = arguments?.let {
            FragmentResultArgs.fromBundle(it)
        }
// taking data from fragment quiz using args object
        args?.let {
            correctNumber = it.correct.toFloat()
            emptyNumber = it.empty.toFloat()
            wrongNumber = it.wrong.toFloat()

        }
//  to show all of this on our chart.
//  so for individual data, that is, for each bar I'll need to create an array list
        val barEntriesArrayListCorrect = ArrayList<BarEntry>()
        val barEntriesArrayListEmpty = ArrayList<BarEntry>()
        val barEntriesArrayListWrong = ArrayList<BarEntry>()

        barEntriesArrayListCorrect.add(BarEntry(0F,correctNumber))
        barEntriesArrayListEmpty.add(BarEntry(1F,emptyNumber))
        barEntriesArrayListWrong.add(BarEntry(2F,wrongNumber))

// We'll need to create the data set for each of these
        val barDataSetCorrect = BarDataSet(barEntriesArrayListCorrect,"Correct Number").apply {
            color = Color.GREEN
            valueTextSize = 24F
            setValueTextColors(arrayListOf(Color.BLACK))
        }
        val barDataSetEmpty = BarDataSet(barEntriesArrayListEmpty,"Empty Number").apply {
            color = Color.BLUE
            valueTextSize = 24F
            setValueTextColors(arrayListOf(Color.BLACK))
        }
        val barDataSetWrong = BarDataSet(barEntriesArrayListWrong,"Wrong Number").apply {
            color = Color.RED
            valueTextSize = 24F
            setValueTextColors(arrayListOf(Color.BLACK))
        }

// now we will create bar data to add these data to the chart
        val barData = BarData(barDataSetCorrect,barDataSetEmpty,barDataSetWrong)

        fragmentresultBinding.resultChart.data = barData

        fragmentresultBinding.buttonNewQuiz.setOnClickListener{

            this.findNavController().popBackStack(R.id.fragmentHome,inclusive = false)

        }
        fragmentresultBinding.buttonExit.setOnClickListener{

            requireActivity().finish()

        }

        return fragmentresultBinding.root
    }

}