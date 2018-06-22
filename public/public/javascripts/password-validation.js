// // Copyright (C) 2016-2017 the original author or authors.
// // See the LICENCE.txt file distributed with this work for additional
// // information regarding copyright ownership.
// //
// // Licensed under the Apache License, Version 2.0 (the "License");
// // you may not use this file except in compliance with the License.
// // You may obtain a copy of the License at
// //
// // http://www.apache.org/licenses/LICENSE-2.0
// //
// // Unless required by applicable law or agreed to in writing, software
// // distributed under the License is distributed on an "AS IS" BASIS,
// // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// // See the License for the specific language governing permissions and
// // limitations under the License.
//
// $(document).ready(function(){
//     $('#js-password-validation').removeClass('hidden').addClass('show');
//
//     $('#password').keyup(function() {
//         var passwordVal = $(this).val();
//
//         var upperCase= new RegExp('[A-Z]');
//         var lowerCase= new RegExp('[a-z]');
//         var numbers = new RegExp('[0-9]');
//
//         if(passwordVal.match(upperCase) && passwordVal.match(lowerCase) && passwordVal.match(numbers) && passwordVal.length >= 10) {
//             $('#capital-letter').removeClass('text-muted').addClass('text-success');
//             $('#capital-letter-glyph').removeClass('glyphicon-minus').addClass('glyphicon-ok');
//             $('#lower-case-letter').removeClass('text-muted').addClass('text-success');
//             $('#lower-case-letter-glyph').removeClass('glyphicon-minus').addClass('glyphicon-ok');
//             $('#number').removeClass('text-muted').addClass('text-success');
//             $('#number-glyph').removeClass('glyphicon-minus').addClass('glyphicon-ok');
//             $('#length').removeClass('text-muted').addClass('text-success');
//             $('#length-glyph').removeClass('glyphicon-minus').addClass('glyphicon-ok');
//         } else {
//             if(passwordVal.match(upperCase)) {
//                 $('#capital-letter').removeClass('text-muted').addClass('text-success');
//                 $('#capital-letter-glyph').removeClass('glyphicon-minus').addClass('glyphicon-ok');
//             } else {
//                 $('#capital-letter').removeClass('text-success').addClass('text-muted');
//                 $('#capital-letter-glyph').removeClass('glyphicon-ok').addClass('glyphicon-minus');
//             }
//
//             if(passwordVal.match(lowerCase)) {
//                 $('#lower-case-letter').removeClass('text-muted').addClass('text-success');
//                 $('#lower-case-letter-glyph').removeClass('glyphicon-minus').addClass('glyphicon-ok');
//             } else {
//                 $('#lower-case-letter').removeClass('text-success').addClass('text-muted');
//                 $('#lower-case-letter-glyph').removeClass('glyphicon-ok').addClass('glyphicon-minus');
//             }
//
//             if(passwordVal.match(numbers)) {
//                 $('#number').removeClass('text-muted').addClass('text-success');
//                 $('#number-glyph').removeClass('glyphicon-minus').addClass('glyphicon-ok');
//             } else {
//                 $('#number').removeClass('text-success').addClass('text-muted');
//                 $('#number-glyph').removeClass('glyphicon-ok').addClass('glyphicon-minus');
//             }
//
//             if(passwordVal.length >= 10) {
//                 $('#length').removeClass('text-muted').addClass('text-success');
//                 $('#length-glyph').removeClass('glyphicon-minus').addClass('glyphicon-ok');
//             } else {
//                 $('#length').removeClass('text-success').addClass('text-muted');
//                 $('#length-glyph').removeClass('glyphicon-ok').addClass('glyphicon-minus');
//             }
//         }
//     });
//
//     $('#confirmPassword').keyup(function(){
//         var passwordVal = $('#password').val();
//         var confirmPasswordVal = $(this).val();
//
//         if(passwordVal === confirmPasswordVal) {
//             $('#match').removeClass('text-muted').addClass('text-success');
//             $('#match-glyph').removeClass('glyphicon-minus').addClass('glyphicon-ok');
//         } else {
//             $('#match').removeClass('text-success').addClass('text-muted');
//             $('#match-glyph').removeClass('glyphicon-ok').addClass('glyphicon-minus');
//         }
//     });
// });
