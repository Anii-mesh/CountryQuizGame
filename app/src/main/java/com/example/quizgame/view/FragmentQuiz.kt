package com.example.quizgame.view

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.quizgame.R
import com.example.quizgame.database.FlagsDao
import com.example.quizgame.databinding.FragmentQuizBinding
import com.example.quizgame.model.FlagsModel
import com.techmania.flagquizwithsqlitedemo.DatabaseCopyHelper


class FragmentQuiz : Fragment() {

    lateinit var fragmentQuizBinding: FragmentQuizBinding
    var flagList = ArrayList<FlagsModel>()

    var correctNumber = 0
    var wrongNumber = 0
    var emptyNumber = 0
    var questionNumber = 0

    lateinit var correctFlag : FlagsModel
    var wrongFlags = ArrayList<FlagsModel>()

    val dao = FlagsDao()

    var optionControl = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentQuizBinding = FragmentQuizBinding.inflate(inflater,container,false)
// moved to global to use again and again
//        try {
//            val dao = FlagsDao()
        flagList = dao.getRandomTenRecords(DatabaseCopyHelper(requireActivity()))
//        } catch (e: Exception) {
//            Log.e("DatabaseError", "Error accessing database: ${e.message}")
//        }


        //just to see the record in record list
        for (flag in flagList){
            Log.d("flags",flag.id.toString())
            Log.d("flags",flag.countryName)
            Log.d("flags",flag.flagName)
            Log.d("flags","*********************")
        }
        showData()

        fragmentQuizBinding.buttonA.setOnClickListener{
            answerControl(fragmentQuizBinding.buttonA)
        }
        fragmentQuizBinding.buttonB.setOnClickListener{
            answerControl(fragmentQuizBinding.buttonB)
        }
        fragmentQuizBinding.buttonC.setOnClickListener{
            answerControl(fragmentQuizBinding.buttonC)
        }
        fragmentQuizBinding.buttonD.setOnClickListener{
            answerControl(fragmentQuizBinding.buttonD)
        }
        fragmentQuizBinding.buttonNext.setOnClickListener{

            questionNumber++

            if(questionNumber > 9){
                // not answered last question
                if(!optionControl){
                    emptyNumber++
                }

                val direction = FragmentQuizDirections.actionFragmentQuizToFragmentResult().apply {
                  // setCorrect(correctNumber) 0r
                    correct = correctNumber
                    wrong = wrongNumber
                    empty = emptyNumber

                }
                this.findNavController().apply {
                    navigate(direction)
                    // to remove the quiz fragment from the back stack so when we come back directly start page will open
                    // here false means destination folder will not be removed
                    popBackStack(R.id.fragmentResult,false)
                }

                Toast.makeText(requireActivity(),"The quiz is finished",Toast.LENGTH_SHORT).show()

            }else{
                // will run until user reaches the last question
                showData()

                if(!optionControl){
                    emptyNumber++
                    fragmentQuizBinding.textViewEmpty.text = emptyNumber.toString()
                }else{
                    setButtonToInitialProperties()
                }

            }
            optionControl = false

        }

        return fragmentQuizBinding.root
    }

    // function to show the data in components

    private fun showData() {

        fragmentQuizBinding.textViewQuestion.text =
            resources.getString(R.string.question_number).plus(" ").plus(questionNumber + 1)

        // Transfer the data that we're retrieving from the database to a variable respectively. so we create a object from model class above

        // first element of the array flag list will be transferred to the correct flag variable.
        correctFlag = flagList[questionNumber]
        //  So now we'll load the flags we receive from the database to the image view flag component with me.
        // and then here we have to create a dynamic structure like this.

        //First we obtain the flag name from the database. And then we'll find the flag
        // with this name from the drawable folder and show it in the image view.
        fragmentQuizBinding.imageViewFlag.setImageResource(
            resources.getIdentifier(
                correctFlag.flagName,
                "drawable",
                requireActivity().packageName
            )
        )
        // correct image uploaded on the image view flag component

        // now we need to get three wrong options from the database for this we create an array above

        wrongFlags =
            dao.getRandomThreeRecords(DatabaseCopyHelper(requireActivity()), correctFlag.id)
        // now we retrived wrong answer from database now we will write these into options

        // keeps data randomly - hashset
        val mixOptions = HashSet<FlagsModel>()
        mixOptions.clear()

        mixOptions.add(correctFlag)
        mixOptions.add(wrongFlags[0])
        mixOptions.add(wrongFlags[1])
        mixOptions.add(wrongFlags[2])

//        we cannot print the data in the hash set by index number.See this is a feature of the hash set array.
//        So for this reason we'll just create another array list here.To transfer the data in the hash set to the array list.

        val options = ArrayList<FlagsModel>()
        options.clear()
//        creating a for loop to transfer the elements of the mix options hash set into the options
        for (eachFlag in mixOptions) {
            options.add(eachFlag)
        }
        //Now I'll need to print the data to text of these options.
        fragmentQuizBinding.buttonA.text = options[0].countryName
        fragmentQuizBinding.buttonB.text = options[1].countryName
        fragmentQuizBinding.buttonC.text = options[2].countryName
        fragmentQuizBinding.buttonD.text = options[3].countryName

    }

    private fun answerControl(button: Button){
        val clickedOption = button.text.toString()
        val correctAnswer = correctFlag.countryName

        if(clickedOption == correctAnswer){

            correctNumber++
           // then I'll write the correct number to the text view correct.
            fragmentQuizBinding.textViewCorrect.text = correctNumber.toString()
            button.setBackgroundColor(Color.GREEN)

        }else{
            wrongNumber++
            fragmentQuizBinding.textViewWrong.text = wrongNumber.toString()
            button.setBackgroundColor(Color.RED)
            button.setTextColor(Color.WHITE)
//In addition, I should make the background color of the correct answer option green
            when(correctAnswer){

                fragmentQuizBinding.buttonA.text -> fragmentQuizBinding.buttonA.setBackgroundColor(Color.GREEN)
                fragmentQuizBinding.buttonB.text -> fragmentQuizBinding.buttonB.setBackgroundColor(Color.GREEN)
                fragmentQuizBinding.buttonC.text -> fragmentQuizBinding.buttonC.setBackgroundColor(Color.GREEN)
                fragmentQuizBinding.buttonD.text -> fragmentQuizBinding.buttonD.setBackgroundColor(Color.GREEN)

            }

        }
        // setting clickable property to false in order to prevent from choosing another option
        fragmentQuizBinding.buttonA.isClickable = false
        fragmentQuizBinding.buttonB.isClickable = false
        fragmentQuizBinding.buttonC.isClickable = false
        fragmentQuizBinding.buttonD.isClickable = false
        // to check any option is clicked or not
        optionControl = true

    }
// to make same properties after moving on next question changes should disappear
    private fun setButtonToInitialProperties(){
        fragmentQuizBinding.buttonA.apply {
            setBackgroundColor(Color.WHITE)
            setTextColor(resources.getColor(R.color.pink,requireActivity().theme))
            isClickable = true
        }
    fragmentQuizBinding.buttonB.apply {
        setBackgroundColor(Color.WHITE)
        setTextColor(resources.getColor(R.color.pink,requireActivity().theme))
        isClickable = true
    }
    fragmentQuizBinding.buttonC.apply {
        setBackgroundColor(Color.WHITE)
        setTextColor(resources.getColor(R.color.pink,requireActivity().theme))
        isClickable = true
    }
    fragmentQuizBinding.buttonD.apply {
        setBackgroundColor(Color.WHITE)
        setTextColor(resources.getColor(R.color.pink,requireActivity().theme))
        isClickable = true
    }

    }

}