# Penjelasan Claims pada JWT

Dalam JWT (JSON Web Token), klaim (claims) adalah informasi yang terkandung dalam token yang mewakili sejumlah pernyataan tentang entitas yang diberi token. Klaim ini mengandung data terstruktur yang memberikan informasi tambahan tentang entitas atau mengatur sejumlah aspek tertentu terkait token.

Ada tiga jenis klaim yang didefinisikan secara standar dalam spesifikasi JWT:

## Registered Claims (Klaim Terdaftar): Klaim terdaftar adalah klaim yang telah ditentukan oleh spesifikasi JWT. Beberapa klaim terdaftar yang umum adalah:

iss (Issuer): Menunjukkan pihak yang mengeluarkan token.
sub (Subject): Menunjukkan subjek atau entitas yang tokennya berlaku untuk.
aud (Audience): Menunjukkan audiens yang dituju oleh token.
exp (Expiration Time): Menunjukkan waktu kedaluwarsa token.
nbf (Not Before): Menunjukkan waktu sebelum token menjadi valid.
iat (Issued At): Menunjukkan waktu saat token dikeluarkan.

## Public Claims (Klaim Publik):

Klaim publik adalah klaim yang ditentukan oleh pihak yang menggunakan JWT, tetapi tidak terdaftar dalam spesifikasi JWT. Klaim ini digunakan untuk memberikan informasi tambahan yang spesifik untuk aplikasi atau sistem tertentu. Nama klaim publik sebaiknya diawali dengan namespace yang unik untuk menghindari konflik dengan klaim terdaftar atau klaim dari pihak lain.

## Private Claims (Klaim Pribadi):

Klaim pribadi adalah klaim yang digunakan secara pribadi oleh pihak yang menerbitkan token. Klaim ini tidak ditentukan dalam spesifikasi JWT maupun oleh pihak lain. Klaim pribadi dapat berisi informasi tambahan yang spesifik untuk aplikasi atau sistem tertentu.

Klaim dalam JWT dapat berisi data dalam bentuk string, boolean, angka, atau struktur data JSON yang lebih kompleks.

============================================================================================================
