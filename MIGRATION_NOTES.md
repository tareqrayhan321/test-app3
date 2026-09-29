# সংশোধনী নোট — CLAUDE_ADVICE.md অডিটের ভিত্তিতে

আপনার দেওয়া `CLAUDE_ADVICE.md` ফাইলে একটা গুরুত্বপূর্ণ, সঠিক পর্যবেক্ষণ ছিল যা আমি নিজে
`index.html`-এ যাচাই করে নিশ্চিত করেছি এবং ঠিক করেছি।

## সবচেয়ে গুরুত্বপূর্ণ সংশোধন (P0): ডেটা স্থাপত্য

**সমস্যা যা ছিল:** আমি প্রতিটা ক্যাটাগরির জন্য আলাদা Firestore কালেকশন বানিয়েছিলাম
(`entries/{category}/items`, `weekly_reports`, `notifications`, `fabric_groups`)। কিন্তু
আসল ওয়েব অ্যাপ সব ডেটা **একটামাত্র ডকুমেন্টে** রাখে: `imran_store/backup`। এর মানে —

- আসল অ্যাপে যে ব্যবসার ডেটা ইতিমধ্যে জমা আছে, সেটা নতুন Android অ্যাপে **একদমই দেখা যেত না**
- Android অ্যাপ থেকে নতুন এন্ট্রি দিলে সেটা ওয়েব অ্যাপে **কখনো সিঙ্ক হতো না**
- দুটো অ্যাপ কার্যত সম্পূর্ণ আলাদা দুটো ডেটাবেইজ ব্যবহার করত

**যা ঠিক করা হয়েছে:**
- নতুন `data/backup/BackupPayload.kt` — ওয়েব অ্যাপের `_FB_KEYS`/`_FB_ARR_KEYS`/`_FB_OBJ_KEYS`
  অনুযায়ী হুবহু কাঠামো (`is_c1_list`..`is_c4_list`, `fabricPurchaseData_v2`, `chart_data`,
  `profitStripData_v1`, `is_deleted_ids`)
- নতুন `data/backup/BackupRepository.kt` — একটামাত্র `imran_store/backup` ডকুমেন্ট
  read-modify-write করে, ওয়েব অ্যাপের `dbPush()`-এর প্যাটার্ন অনুসরণ করে
- নতুন `data/backup/BackupMappers.kt` — raw ব্যাকআপ টাইপ ও UI-friendly domain model
  (Entry/FabricGroup/WeeklyReport) এর মধ্যে রূপান্তর
- সব ৯টা ViewModel (HomeViewModel, FvListViewModel, DetailViewModel, DebtorsListViewModel,
  WeeklyReportsViewModel, PaikkariViewModel, FabricViewModel, DbConnectViewModel,
  NotificationViewModel) পুনর্লিখে `BackupRepository` ব্যবহার করানো হয়েছে
- পুরনো ভুল `data/repository/FirestoreRepository.kt` সম্পূর্ণ মুছে ফেলা হয়েছে

## অন্যান্য সংশোধন

**id: String → Long।** ওয়েব অ্যাপ `Date.now()` দিয়ে সংখ্যা id বানায় এবং সেই id দিয়েই
merge/dedup হয়। `Entry`, `WeeklyReport`, `FabricGroup`, `FabricPurchase` — সবগুলোর `id`
এখন `Long`। নেভিগেশন রুট আর্গুমেন্টও `NavType.LongType`-এ পরিবর্তন করা হয়েছে।

**History-এর bill/baki অসামঞ্জস্য।** ওয়েব অ্যাপে history আইটেমে বকেয়ার পরিমাণ কাস্টমারের
ক্ষেত্রে `h.bill`-এ, মহাজনের ক্ষেত্রে `h.baki`-তে থাকে (নিজেই ভিন্ন ফিল্ড ব্যবহার করে)। এই
অসামঞ্জস্যটা `HistoryItem`-এ দুটো আলাদা ফিল্ড (`bill`, `baki`) ও
`bokeyoaAmount(isCustomer)` হেল্পার দিয়ে সংরক্ষণ করা হয়েছে — `TransactionRows.kt` ও
`DetailViewModel.kt` এখন ক্যাটাগরি অনুযায়ী সঠিক ফিল্ড পড়ে/লেখে।

**chart_data-এর merge লজিক।** `year+week` মিলে গেলে replace, নাহলে prepend, তারপর
সর্বোচ্চ ২৬টায় ক্যাপ (সবচেয়ে পুরাতনটা বাদ) — ওয়েব অ্যাপের `saveToChart()`-এর হুবহু আচরণ।
`WeeklyReport`-এ `month` ফিল্ডও যোগ করা হয়েছে যা আগে ছিল না।

**নোটিফিকেশন এখন লোকাল-অনলি।** ওয়েব অ্যাপে `app_notifications` Firestore-এ sync হয় না
(`_FB_KEYS` ইত্যাদির কোনোটাতেই নেই) — প্রতিটা ডিভাইসে আলাদা। আগে আমি ভুলভাবে এটাকে
Firestore `notifications` কালেকশনে রেখেছিলাম (মাল্টি-ডিভাইস সিঙ্ক হয়ে যেত, যা ওয়েব অ্যাপে
হয় না)। এখন `NotificationCenter` — একটা প্রসেস-স্কোপড ইন-মেমরি সিঙ্গলটন — ব্যবহার করা হচ্ছে,
যা ওয়েব অ্যাপের "শুধু এই ডিভাইসে" আচরণের কাছাকাছি।

**profitStripData_v1** — KPI স্ট্রিপের net/gross এখন সরাসরি ব্যাকআপ পেলোডে সেভ হয়
(পাইকারি ক্যালকুলেটর থেকে), ওয়েব অ্যাপের `saveProfitStripData()`-এর সমতুল্য।

## এই দফায় সমাধান করা হয়েছে

- **`is_deleted_ids` tombstone-এর সম্পূর্ণ merge-avoid লজিক** — `BackupRepository.applyTombstones()`
  এখন প্রতিটা read (`observeBackup()`, `fetchOnce()` এর পরে `updatePayload()`-এর ভেতরে) ও
  প্রতিটা write-এর আগে `is_c1_list`..`is_c4_list` ও `fabricPurchaseData_v2` থেকে
  `is_deleted_ids`-এ থাকা id বাদ দেয় — ওয়েব অ্যাপের `onSnapshot` listener-এর
  `filtered = data[k].filter(r => !deletedForKey.has(r.id))` চেকের হুবহু সমতুল্য। এতে অন্য
  ডিভাইসে ডিলিট হওয়া কোনো আইটেম স্টেল লোকাল state থেকে ভুলবশত আর ফিরে আসতে পারবে না।
- **ডেটা-শেয়ারিং ব্যাখ্যা** — যেহেতু মূল P0 ফিক্স (একক `imran_store/backup` ডকুমেন্ট) দিয়ে
  Android অ্যাপ ও ওয়েব অ্যাপ এমনিতেই একই ডেটা শেয়ার করে, তাই আলাদা "ইম্পোর্ট টুল" এর
  দরকার নেই — ওয়েব অ্যাপে কোনো JSON export/import ফিচারও নেই যা মেলাতে হবে। এর বদলে
  DB Connect Modal-এ একটা স্পষ্ট তথ্য-বক্স যোগ করা হয়েছে যা ব্যাখ্যা করে যে দুটো অ্যাপ একই
  Firebase প্রজেক্টে কানেক্ট থাকলে একই ডেটা দেখাবে, এবং কোনো ইম্পোর্ট/এক্সপোর্ট ধাপ লাগবে না।

## এখনো যা বাকি (ঐচ্ছিক ভবিষ্যৎ উন্নতি)

- সব লেখাপড়া `applyTombstones()`-এর মধ্য দিয়ে যাওয়ায় সামান্য বাড়তি ফিল্টারিং ওভারহেড আছে,
  কিন্তু ডেটাসেট সাইজ (কয়েকশ এন্ট্রি) বিবেচনায় এটা নগণ্য।
- `is_deleted_ids`-এর তালিকা কখনো নিজে থেকে ছোট হয় না (ওয়েব অ্যাপেও হয় না) — বহু বছর
  ব্যবহারের পর এই ম্যাপ বড় হতে পারে। ওয়েব অ্যাপেও এই একই আচরণ, তাই এটা বাগ নয়, বরং
  মূল অ্যাপের ডিজাইন সিদ্ধান্তের অংশ।
