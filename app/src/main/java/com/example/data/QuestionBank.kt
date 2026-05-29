package com.example.data

object QuestionBank {
    val questions = listOf(
        NJQuestion(
            id = 1,
            category = "GDL & Permit Rules",
            questionText = "What are the hours of driving restriction for a Special Learner Permit or Examination Permit holder (under age 21) in New Jersey?",
            options = listOf(
                "No restrictions apply",
                "11:01 PM to 5:00 AM",
                "10:00 PM to 6:00 AM",
                "Midnight to 6:00 AM"
            ),
            correctAnswerIndex = 1,
            explanation = "Any Special Learner Permit or Examination Permit holder under the age of 21 is restricted from driving between 11:01 PM and 5:00 AM. If the permit holder is age 21 or older, hour and passenger restrictions do not apply."
        ),
        NJQuestion(
            id = 2,
            category = "GDL & Permit Rules",
            questionText = "How many passengers is a Probationary License holder (under age 21) allowed to have in the vehicle?",
            options = listOf(
                "As many as the seat belts can safely secure",
                "One passenger, unless accompanied by a parent or guardian",
                "No passengers are allowed in the car",
                "Dependents of the driver plus one additional person, unless accompanied by a parent or guardian"
            ),
            correctAnswerIndex = 3,
            explanation = "Under the GDL rules, a probationary license holder under 21 is permitted to transport dependents of the driver and one additional passenger, unless accompanied by a parent or guardian."
        ),
        NJQuestion(
            id = 3,
            category = "GDL & Permit Rules",
            questionText = "In addition to a valid valid permit/license, what must GDL drivers under age 21 display on their license plates?",
            options = listOf(
                "Two reflective red decals (one on the front and one on the rear plate)",
                "One yellow decal on the rear plate only",
                "A green sticker on the windshield",
                "Nothing, license plates are sufficient"
            ),
            correctAnswerIndex = 0,
            explanation = "All GDL drivers under age 21 who possess a permit or probationary license must display two reflective red decals: one on the top-left corner of the front license plate and one on the top-left corner of the rear license plate."
        ),
        NJQuestion(
            id = 4,
            category = "GDL & Permit Rules",
            questionText = "What is the fine for failing to comply with any of the GDL license/permit restrictions?",
            options = listOf(
                "$50",
                "$100",
                "$250",
                "$500 and license suspension"
            ),
            correctAnswerIndex = 1,
            explanation = "A violation of any of the GDL permit or probationary license conditions is subject to a fine of $100."
        ),
        NJQuestion(
            id = 5,
            category = "Speed Limits & Safe Driving",
            questionText = "Unless otherwise posted, what is the speed limit in a school zone, business, or residential district in New Jersey?",
            options = listOf(
                "15 mph",
                "25 mph",
                "30 mph",
                "35 mph"
            ),
            correctAnswerIndex = 1,
            explanation = "In New Jersey, the standard speed limit in school zones, business districts, and residential areas is 25 miles per hour unless a different limit is posted."
        ),
        NJQuestion(
            id = 6,
            category = "Under the Influence",
            questionText = "For drivers under the age of 21, New Jersey state law defines driving under the influence (DUI) as having a Blood Alcohol Concentration (BAC) of:",
            options = listOf(
                "0.01% or higher",
                "0.05% or higher",
                "0.08% or higher",
                "0.10% or higher"
            ),
            correctAnswerIndex = 0,
            explanation = "For drivers under age 21, New Jersey has a 'Zero Tolerance' law. Any BAC of 0.01% or higher constitutes driving under the influence."
        ),
        NJQuestion(
            id = 7,
            category = "Under the Influence",
            questionText = "What is the legal BAC limit for drivers over the age of 21 in New Jersey?",
            options = listOf(
                "0.02%",
                "0.05%",
                "0.08%",
                "0.10%"
            ),
            correctAnswerIndex = 2,
            explanation = "For drivers age 21 and older, it is illegal to operate a motor vehicle with a Blood Alcohol Concentration (BAC) of 0.08% or higher."
        ),
        NJQuestion(
            id = 8,
            category = "Under the Influence",
            questionText = "If you refuse to take a breathalyzer test in New Jersey, what are the consequences under the Implied Consent Law?",
            options = listOf(
                "A warning for the first offence",
                "An automatic arrest and 6 months jail time",
                "It is equivalent to driving with a BAC of 0.10% for a first offense; includes loss of driving privileges, installation of an ignition interlock, and MVC insurance surcharges of $1,000 per year for 3 years",
                "A small fine of $50 and no license points"
            ),
            correctAnswerIndex = 2,
            explanation = "New Jersey's Implied Consent Law means by driving on public roads, you agree to take a breath test. Refusing a breath test is equivalent to a first-offense DUI with a BAC of 0.10%, carrying severe license penalties, mandatory ignition interlock, and a $1,000 annual surcharge for three years."
        ),
        NJQuestion(
            id = 9,
            category = "Under the Influence",
            questionText = "If your BAC reaches 0.05%, how much does your risk of causing a car crash multiply?",
            options = listOf(
                "It doubles",
                "It triples",
                "It becomes 6 times greater",
                "It becomes 25 times greater"
            ),
            correctAnswerIndex = 0,
            explanation = "At a BAC of 0.05%, the risk of causing a vehicle accident doubles. At 0.10%, the risk is 6 times greater, and at 0.15%, the risk is 25 times greater."
        ),
        NJQuestion(
            id = 10,
            category = "Standard Parking & Distances",
            questionText = "You may not park within how many feet of a fire hydrant?",
            options = listOf(
                "5 feet",
                "10 feet",
                "15 feet",
                "25 feet"
            ),
            correctAnswerIndex = 1,
            explanation = "In New Jersey, you cannot park your vehicle within 10 feet of a public or private fire hydrant."
        ),
        NJQuestion(
            id = 11,
            category = "Standard Parking & Distances",
            questionText = "You may not park within how many feet of a crosswalk at an intersection?",
            options = listOf(
                "10 feet",
                "15 feet",
                "25 feet",
                "50 feet"
            ),
            correctAnswerIndex = 2,
            explanation = "Under New Jersey traffic laws, parking is prohibited within 25 feet of a crosswalk at an intersection or side line of a street crossing."
        ),
        NJQuestion(
            id = 12,
            category = "Standard Parking & Distances",
            questionText = "You may not park within how many feet of a stop sign?",
            options = listOf(
                "10 feet",
                "25 feet",
                "50 feet",
                "75 feet"
            ),
            correctAnswerIndex = 2,
            explanation = "Parking is restricted; you cannot park a vehicle within 50 feet of a stop sign or a traffic signal."
        ),
        NJQuestion(
            id = 13,
            category = "Traffic Signs & Signals",
            questionText = "An eight-sided (octagon) sign means:",
            options = listOf(
                "Yield",
                "Stop",
                "School zone warning",
                "Railroad crossing"
            ),
            correctAnswerIndex = 1,
            explanation = "An octagonal (8-sided) traffic sign is exclusively used for STOP signs, which are always red with white letters."
        ),
        NJQuestion(
            id = 14,
            category = "Traffic Signs & Signals",
            questionText = "A triangular-shaped sign is used to indicate:",
            options = listOf(
                "Yield",
                "Stop",
                "No Passing Zone",
                "Construction Zone"
            ),
            correctAnswerIndex = 0,
            explanation = "A triangular (3-sided) sign is used to indicate a YIELD. It is red and white with red letters."
        ),
        NJQuestion(
            id = 15,
            category = "Traffic Signs & Signals",
            questionText = "A round, yellow and black sign is a warning for:",
            options = listOf(
                "A sharp curve",
                "A school crossing",
                "A railroad crossing ahead",
                "No passing zone"
            ),
            correctAnswerIndex = 2,
            explanation = "A circular yellow sign with a black 'X' and 'RR' letters is used exclusively to warn drivers of an approaching railroad crossing."
        ),
        NJQuestion(
            id = 16,
            category = "Traffic Signs & Signals",
            questionText = "A diamond-shaped sign signifies a:",
            options = listOf(
                "Stop sign",
                "Yield sign",
                "Warning or hazard ahead",
                "Recreational trail"
            ),
            correctAnswerIndex = 2,
            explanation = "Diamond-shaped signs are used as general warning/caution signs. They warn drivers of upcoming road conditions, hazards, or construction."
        ),
        NJQuestion(
            id = 17,
            category = "Road Rules & Operations",
            questionText = "Headlights must be used in New Jersey 1/2 hour after sunset until 1/2 hour before sunrise and also when:",
            options = listOf(
                "You are driving above the speed limit",
                "Whenever your windshield wipers are turned on (due to rain, snow, or fog) or when visibility is less than 500 feet",
                "Only in heavy fog",
                "Between midnight and sunrise only"
            ),
            correctAnswerIndex = 1,
            explanation = "State law requires headlights to be active during rain, snow, ice storms, fog, when visibility is 500 feet or less, and whenever windshield wipers are in use."
        ),
        NJQuestion(
            id = 18,
            category = "GDL & Permit Rules",
            questionText = "A supervising driver for a permit holder under age 21 must meet which of the following requirements?",
            options = listOf(
                "Be at least 18 years old and have a valid driver's license for 1 year",
                "Be at least 21 years old and have a valid New Jersey driver's license for a minimum of 3 years",
                "Be a relative of the permit holder",
                "Be certified as a professional driving instructor"
            ),
            correctAnswerIndex = 1,
            explanation = "In New Jersey, the designated supervising driver for a GDL permit holder under 21 must be at least 21 years old, possess a valid New Jersey driver's license, and have a minimum of 3 years of licensed driving experience."
        ),
        NJQuestion(
            id = 19,
            category = "Road Rules & Operations",
            questionText = "Under New Jersey state law, the driver and all passengers in a passenger vehicle must wear:",
            options = listOf(
                "A seat belt. The driver is responsible for all passengers under age 18.",
                "Seat belts, only if riding in the front seat",
                "Nothing, seat belts are comfortable options",
                "Helmets if driving under probationary conditions"
            ),
            correctAnswerIndex = 0,
            explanation = "All occupants of a passenger vehicle in New Jersey must wear a seat belt. The driver is responsible for ensuring all passengers under 18 wear seat belts or rest in child safety systems."
        ),
        NJQuestion(
            id = 20,
            category = "Road Rules & Operations",
            questionText = "What should you do when an emergency vehicle (police, fire, ambulance) approaches from behind with its lights flashing and sirens sounding?",
            options = listOf(
                "Speed up to stay ahead of them",
                "Slam on your brakes immediately",
                "Steer to the extreme right of the road, stop, and let the vehicle pass safely",
                "Maintain your speed and drive in your lane"
            ),
            correctAnswerIndex = 2,
            explanation = "New Jersey law requires motor vehicle operators to yield the right-of-way to emergency vehicles. Drivers must steer to the extreme right, stop, and remain stationary until the vehicle has passed."
        ),
        NJQuestion(
            id = 21,
            category = "Speed Limits & Safe Driving",
            questionText = "When driving around a curve, what behavior does a vehicle naturally tend to follow?",
            options = listOf(
                "Turn more sharply automatically",
                "Slow down automatically",
                "Keep going straight, necessitating slowing down before entering the curve",
                "Pull toward the inner lane"
            ),
            correctAnswerIndex = 2,
            explanation = "Because of centrifugal force, a vehicle tends to continue in a straight line when entering a curve. It is crucial to slow down before entering the curve to maintain complete steering control."
        ),
        NJQuestion(
            id = 22,
            category = "Road Rules & Operations",
            questionText = "Under New Jersey law, inline skaters, skateboarders, and bicyclists possess the same rights and responsibilities as:",
            options = listOf(
                "Pedestrians",
                "Motor vehicles",
                "Emergency vehicles",
                "They have no rights on public roads"
            ),
            correctAnswerIndex = 1,
            explanation = "In New Jersey, skateboarders, inline skaters, and bicyclists have the same rights, privileges, and duties as operators of motor vehicles on public roadways."
        ),
        NJQuestion(
            id = 23,
            category = "Alcohol & Drugs",
            questionText = "A 12-ounce can of beer has the same amount of alcohol as which of the following?",
            options = listOf(
                "A 5-ounce glass of table wine or a 1.5-ounce shot of 80-proof whiskey",
                "A 1-pint bottle of hard cider",
                "A double shot of espresso",
                "No other drinks; beer has less concentrated alcohol"
            ),
            correctAnswerIndex = 0,
            explanation = "A conventional 12-ounce can of beer carries the exact same volume of pure alcohol as a standard 5-ounce glass of table wine or a 1.5-ounce glass of 80-proof hard whiskey."
        ),
        NJQuestion(
            id = 24,
            category = "GDL & Permit Rules",
            questionText = "How long is a driver on probation after successfully passing the road test and obtaining a GDL Probationary License?",
            options = listOf(
                "6 months",
                "1 year (12 months)",
                "2 years",
                "Until they turn 21"
            ),
            correctAnswerIndex = 1,
            explanation = "Upon passing the road test, the GDL driver receives a Probationary License and must practice unsupervised driving under probationary restrictions for exactly one year before upgrading to a Basic License."
        ),
        NJQuestion(
            id = 25,
            category = "Road Rules & Operations",
            questionText = "The New Jersey 'Move Over' law requires drivers to:",
            options = listOf(
                "Yield to any vehicle passing from behind",
                "Change lanes safely or slow down substantially when approaching stationary emergency vehicles, tow trucks, or highway maintenance vehicles with flashing lights",
                "Move to the left lane on all highways by default",
                "Pull over to the side of the road when it rains"
            ),
            correctAnswerIndex = 1,
            explanation = "The Move Over law requires motorists approaching stationary emergency/utility/tow vehicles with flashing lights to safely change lanes into a lane not adjacent to the stationary vehicle, or slow down if a lane change is impossible or unsafe."
        ),
        NJQuestion(
            id = 26,
            category = "Road Rules & Operations",
            questionText = "When are roads most slippery during a rainfall event?",
            options = listOf(
                "About 1 hour into the heavy rainfall",
                "During the first few minutes of rainfall, as road oils bubble to the surface",
                "Right after the rain stops completely",
                "The slippery rate is consistent throughout the storm"
            ),
            correctAnswerIndex = 1,
            explanation = "Roadways are typically most slippery during the first few minutes of a rainfall because the rainwater mixes with accumulated oils, grease, and exhaust soot on the dry pavement before washing away."
        ),
        NJQuestion(
            id = 27,
            category = "Road Rules & Operations",
            questionText = "A driver should signal their intention to turn at least how far before making the turn?",
            options = listOf(
                "50 feet",
                "100 feet",
                "150 feet",
                "200 feet"
            ),
            correctAnswerIndex = 1,
            explanation = "State law mandates that you must activate your turn signal at least 100 feet before making any turn or lane change."
        ),
        NJQuestion(
            id = 28,
            category = "Road Rules & Operations",
            questionText = "What does a high-beam headlight setting assist with, and when should it be switched to low-beams?",
            options = listOf(
                "Driving in heavy fog; keep them on at all times",
                "Open country driving at night; switch to low-beams when another vehicle is approaching or when you are following a car closely",
                "Driving in residential neighborhoods; never turn them off",
                "Parking in unlit parking spaces"
            ),
            correctAnswerIndex = 1,
            explanation = "High-beam lights should be used of open-country driving when there is no approaching traffic. They must be switched to low-beams within 500 feet of an oncoming vehicle or when following behind another vehicle."
        ),
        NJQuestion(
            id = 29,
            category = "GDL & Permit Rules",
            questionText = "What is the minimum age to obtain a New Jersey Basic Driver's License?",
            options = listOf(
                "16 years old",
                "17 years old",
                "18 years old",
                "21 years old"
            ),
            correctAnswerIndex = 2,
            explanation = "To earn an unrestricted Class D Basic Driver's License in New Jersey, you must be at least 18 years old and have completed all Probationary GDL requirements."
        ),
        NJQuestion(
            id = 30,
            category = "Road Rules & Operations",
            questionText = "In New Jersey, when a school bus has stopped directly in front of a school to pick up or let off children, you may pass from either direction at a speed of no more than:",
            options = listOf(
                "10 mph",
                "15 mph",
                "20 mph",
                "You must stop and cannot pass under any circumstance"
            ),
            correctAnswerIndex = 0,
            explanation = "Generally, you must stop 25 feet away from a school bus with flashing red lights. However, if a school bus has stopped directly in front of a school to pick up or discharge children, you may pass from either direction at a slow speed of no more than 10 mph."
        ),
        NJQuestion(
            id = 31,
            category = "Road Rules & Operations",
            questionText = "At an uncontrolled or multi-way stop intersection, if two vehicles arrive at the exact same moment, who yields to whom?",
            options = listOf(
                "The vehicle on the left yields to the vehicle on the right",
                "The vehicle on the right yields to the vehicle on the left",
                "The larger vehicle goes first",
                "The vehicle traveling straight yields to turning vehicles"
            ),
            correctAnswerIndex = 0,
            explanation = "At an uncontrolled intersection or a multi-way stop, the vehicle on the left must yield the right-of-way to the vehicle on the right."
        ),
        NJQuestion(
            id = 32,
            category = "Standard Parking & Distances",
            questionText = "When parking downhill with a curb, in which direction should you turn your steering wheel?",
            options = listOf(
                "Straight ahead",
                "Away from the curb (toward the street)",
                "Toward the curb (right)",
                "It does not matter if the emergency brake is set"
            ),
            correctAnswerIndex = 2,
            explanation = "When parking downhill next to a curb, you should turn your steering wheel toward the curb (right), so that if the vehicle rolls, the front tire rests securely against the curb edge."
        ),
        NJQuestion(
            id = 33,
            category = "Standard Parking & Distances",
            questionText = "When parking uphill with a curb, in which direction should you turn your steering wheel?",
            options = listOf(
                "Away from the curb (left)",
                "Toward the curb (right)",
                "Straight ahead",
                "Keep wheels parallel to the road"
            ),
            correctAnswerIndex = 0,
            explanation = "When parking uphill next to a curb, you must turn your steering wheel away from the curb (left), so that if the car slides backward, the back of the front tire catches the curb."
        ),
        NJQuestion(
            id = 34,
            category = "Road Rules & Operations",
            questionText = "In New Jersey, unless directed by a police officer, you are strictly prohibited from parking in front of:",
            options = listOf(
                "A library",
                "A public school",
                "A public or private driveway",
                "A grocery store parking lot"
            ),
            correctAnswerIndex = 2,
            explanation = "It is illegal to park in front of a public or private driveway, or on any sidewalk, crosswalk, or safety zone."
        ),
        NJQuestion(
            id = 35,
            category = "Alcohol & Drugs",
            questionText = "The best and only guaranteed method to sober up or reduce a BAC level after drinking alcohol is:",
            options = listOf(
                "Drinking a hot cup of black coffee",
                "Taking a cold shower",
                "Allowing time to pass, which lets the body naturally metabolize the alcohol",
                "Exercising vigorously or walking"
            ),
            correctAnswerIndex = 2,
            explanation = "Only time can make a person sober. Neither hot coffee, fresh air, cold showers, nor active exercise speeds up the body's natural liver metabolism rates."
        ),
        NJQuestion(
            id = 36,
            category = "GDL & Permit Rules",
            questionText = "Under GDL law, during what hours is a Special Learner Permit holder (under age 21) permitted to practice supervised driving?",
            options = listOf(
                "5:01 AM to 11:00 PM",
                "6:00 AM to Midnight",
                "Any time of day as long as a supervisor is present",
                "9:00 AM to 9:00 PM only"
            ),
            correctAnswerIndex = 0,
            explanation = "Permitted practice driving hours are restricted from 5:01 AM to 11:00 PM under GDL rules for Special Learner and Examination Permit holders under 21."
        ),
        NJQuestion(
            id = 37,
            category = "Traffic Signs & Signals",
            questionText = "What does a flashing red light at an intersection indicate?",
            options = listOf(
                "Slow down and proceed with extreme caution",
                "Stop completely, yield right-of-way to oncoming traffic/pedestrians, and proceed only when safe (acts just like a stop sign)",
                "The traffic signal is broken, yield to all vehicles",
                "Speed up to clear the intersection before the light changes"
            ),
            correctAnswerIndex = 1,
            explanation = "A flashing red traffic signal instructs motorists to make a complete stop. They must yield to pedestrians and crossing traffic prior to moving forward, exactly as they would at a STOP sign."
        ),
        NJQuestion(
            id = 38,
            category = "Road Rules & Operations",
            questionText = "A single, solid white line running across an intersection lane means a driver must:",
            options = listOf(
                "Stop behind the line for a traffic signal or stop sign",
                "Yield right-of-way and turn right",
                "Swerve around the line to let others pass",
                "Disregard, it is simple lane painting"
            ),
            correctAnswerIndex = 0,
            explanation = "A solid white line running transversely across an intersection lane defines a 'Stop Line'. Motorists must stop completely behind this line for any traffic light or stop sign."
        ),
        NJQuestion(
            id = 39,
            category = "Road Rules & Operations",
            questionText = "When may a driver pass a vehicle on the right side on a New Jersey roadway?",
            options = listOf(
                "On any two-lane road at any time",
                "Only on streets with more than one lane going in the same direction, or when the motorist ahead is making a left turn and there is sufficient room on the paved road",
                "You are never allowed to pass on the right under any condition",
                "If you blow your horn and slide past on the shoulder of the road"
            ),
            correctAnswerIndex = 1,
            explanation = "You may pass on the right only when there is more than one lane flowing in your direction, or if the driver ahead is turning left and there is a safe, paved shoulder space. Passing on a dirt or unpaved shoulder is strictly illegal."
        ),
        NJQuestion(
            id = 40,
            category = "Fines & Penalties",
            questionText = "If you accumulate six or more points within three years, you are subject to an MVC surcharge. What is the standard cost of this surcharge?",
            options = listOf(
                "$100 for 6 points, and $10 for each additional point",
                "$150 for 6 points, and $25 for each additional point",
                "$250 for 6 points, and $50 for each additional point",
                "$500 for 6 points by default"
            ),
            correctAnswerIndex = 1,
            explanation = "Motorists who accumulate 6 or more points within 3 years are fined a surcharge of $150 for the initial six points, plus an additional $25 for every point beyond six."
        ),
        NJQuestion(
            id = 41,
            category = "GDL & Permit Rules",
            questionText = "How many months of supervised driving practice must a Special Learner Permit holder (under age 21) complete before taking the road test?",
            options = listOf(
                "3 months",
                "6 months",
                "9 months",
                "12 months"
            ),
            correctAnswerIndex = 1,
            explanation = "Under the New Jersey GDL program, Special Learner Permit holders under 21 must complete a minimum of 6 months of supervised, violation-free driving practice before taking the practical road test."
        ),
        NJQuestion(
            id = 42,
            category = "Road Rules & Operations",
            questionText = "What is the standard 'three-second rule' (or three-second-plus rule) used for?",
            options = listOf(
                "Measuring speed at intersections",
                "Estimating safe following distance between your car and the vehicle ahead",
                "Deciding when to make a turn at stop signs",
                "Determining if you can pass a school bus"
            ),
            correctAnswerIndex = 1,
            explanation = "The three-second-plus rule helps motorists measure a safe following margin. It provides sufficient reaction time if the car directly in front stops suddenly under normal dry road conditions."
        ),
        NJQuestion(
            id = 43,
            category = "Road Rules & Operations",
            questionText = "If you experience a sudden tire blowout while driving on the highway, you should:",
            options = listOf(
                "Slam on the brakes to stop as fast as possible",
                "Grip the steering wheel firmly, hold it straight, gradually ease off the gas pedal, and let the vehicle coast to a stop on the side of the road",
                "Turn the wheel sharply in the direction of the blown tire",
                "Accelerate to keep the car stable"
            ),
            correctAnswerIndex = 1,
            explanation = "During a tire blowout, keep a firm grip on the steering wheel, keep the vehicle straight, do not apply brakes, gradually release the accelerator pedal, and pull over safely as the vehicle slows down naturally."
        ),
        NJQuestion(
            id = 44,
            category = "Traffic Signs & Signals",
            questionText = "What are the colors of a standard pedestrian crosswalk warning sign (and school zone indicator)?",
            options = listOf(
                "Orange and black",
                "Yellow-green or yellow, with black symbols",
                "Blue and white",
                "Red and white"
            ),
            correctAnswerIndex = 1,
            explanation = "Pedestrian crossing warnings and school zone indicator signs are distinctively colored fluorescent yellow-green or bright yellow, decorated with black symbols."
        ),
        NJQuestion(
            id = 45,
            category = "Road Rules & Operations",
            questionText = "Under New Jersey state law, what hand signal indicates a stop or slow down?",
            options = listOf(
                "Arm extended straight out of the window",
                "Hand and arm downward, palm facing the rear",
                "Hand and arm upward",
                "Waving your hand back and forth"
            ),
            correctAnswerIndex = 1,
            explanation = "For hand signals: Hand and arm downward (palm pointing backward) indicates stopping/slowing. Hand and arm upward indicates a right turn. Arm extended straight out indicates a left turn."
        ),
        NJQuestion(
            id = 46,
            category = "Road Rules & Operations",
            questionText = "When is a driver allowed to turn right on a red light in New Jersey?",
            options = listOf(
                "At any time or intersection without stopping",
                "After making a complete stop, checking for pedestrians and cross-traffic, unless a 'No Turn on Red' sign is posted",
                "Only if another driver waves you through",
                "You can never turn right on red in New Jersey"
            ),
            correctAnswerIndex = 1,
            explanation = "A right turn on red is permitted in New Jersey unless a 'No Turn on Red' sign is explicitly posted. You must make a complete stop, yield to pedestrians, and make sure the intersection is entirely safe before turning."
        ),
        NJQuestion(
            id = 47,
            category = "Speed Limits & Safe Driving",
            questionText = "On New Jersey secondary state roads, what is the standard speed limit if no speed indicators are posted?",
            options = listOf(
                "35 mph",
                "45 mph",
                "50 mph",
                "60 mph"
            ),
            correctAnswerIndex = 2,
            explanation = "Unless a speed limit sign is present, the statutory speed limit on New Jersey non-posted state highways or rural secondary roads is 50 mph."
        ),
        NJQuestion(
            id = 48,
            category = "GDL & Permit Rules",
            questionText = "What physical items must you bring with you when taking the practical behind-the-wheel road test?",
            options = listOf(
                "A vehicle with an easy secondary emergency brake accessible to the examiner, a valid permit, 2 red decals, and current registration/insurance documents",
                "Only yourself and a permit",
                "Any vehicle as long as it has a working engine",
                "A note of approval from your high school instructor"
            ),
            correctAnswerIndex = 0,
            explanation = "For the road test, the examiner must have unobstructed access to a foot brake or parking brake located between the seats. You must bring a primary licensed driver, a valid permit, registration, insurance, and GDL red decals."
        ),
        NJQuestion(
            id = 49,
            category = "Alcohol & Drugs",
            questionText = "Studies and convictions in New Jersey reveal that the majority of motorist arrests for driving under the influence have been drinking:",
            options = listOf(
                "Wine",
                "Beer",
                "Mixed hard liquor cocktails",
                "Hard cider"
            ),
            correctAnswerIndex = 1,
            explanation = "Statistically, individuals convicted of driving under the influence in New Jersey are most frequently caught drinking beer, which is widely available and often underestimated."
        ),
        NJQuestion(
            id = 50,
            category = "Road Rules & Operations",
            questionText = "Under New Jersey law, what is the safety distance required when meeting or following an active school bus on a public road with flashing red lights?",
            options = listOf(
                "Stop at least 15 feet away",
                "Stop at least 25 feet away",
                "Slow down and pass at 15 mph",
                "Swerve onto the left lane and proceed with caution"
            ),
            correctAnswerIndex = 1,
            explanation = "When a school bus has stopped with flashing red lights on a public road, motorists must stop their vehicles at least 25 feet away from the bus in either direction."
        )
    )
}
