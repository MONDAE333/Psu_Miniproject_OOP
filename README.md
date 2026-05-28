# PSU Miniproject OOP - POS System (Text-Based)

## 📋 รายละเอียด Project
โปรแกรม POS (Point of Sale) แบบ Text-Based ที่เป็นการจำลองระบบการขายของร้าน **Moshi Moshi** 
โปรเจคนี้ออกแบบมาเพื่อฝึกการพัฒนาระบบตามหลักการ Object-Oriented Programming (OOP) 
โดยเน้นการแยก Class ที่เหมาะสมและการสืบทอด (Inheritance) ให้ถูกต้อง

> **หมายเหตุ:** โปรแกรมนี้เป็นเวอร์ชั่นจำลองอย่างง่าย ไม่ได้ใช้ข้อมูลระบบจริงของร้าน 100%

## 🎯 วัตถุประสงค์
- ✅ ฝึกการออกแบบระบบตามหลักการ OOP
- ✅ เรียนรู้การแยก Responsibility ของแต่ละ Class
- ✅ ประยุกต์ใช้ Inheritance (สืบทอด) อย่างถูกต้อง
- ✅ พัฒนาทักษะการจัดการข้อมูลและสถานะของระบบ

## 🛠️ เทคโนโลยี
- **ภาษา:** Java
- **ประเภท:** Console Application (Text-Based)
- **หลักการหลัก:** Object-Oriented Programming (OOP)

## 📁 โครงสร้าง Project

```
Psu_Miniproject_OOP/
├── src/
│   ├── Main.java              # Entry point ของโปรแกรม
│   ├── Product.java           # Class สำหรับจัดการข้อมูลสินค้า
│   ├── Bill.java              # Class สำหรับจัดการบิล/ใบเสร็จ
│   ├── MenuItem.java          # Class สำหรับรายการเมนู
│   ├── Order.java             # Class สำหรับจัดการคำสั่งซื้อ
│   └── ... (อื่น ๆ)
├── README.md
└── ...
```

## 🚀 วิธีการใช้งาน

### ข้อกำหนดเบื้องต้น
- Java Development Kit (JDK) 8 หรือเวอร์ชั่นที่สูงกว่า

### การรันโปรแกรม

```bash
# 1. ไปที่ directory ของ project
cd Psu_Miniproject_OOP

# 2. คอมไพล์ไฟล์ Java ทั้งหมด
javac src/*.java

# 3. รัน Main Program
java -cp src Main
```

## 💡 ฟีเจอร์หลัก

### 1. **การจัดการสินค้า (Product Management)**
   - เพิ่ม/ลบ/แก้ไขสินค้า
   - จัดเก็บข้อมูลสินค้า (ชื่อ, ราคา, จำนวน)

### 2. **การสร้างบิล (Bill Generation)**
   - สร้างใบเสร็จจากสินค้าที่เลือก
   - คำนวณราคารวม ส่วนลด ภาษี

### 3. **ระบบคำสั่งซื้อ (Order System)**
   - รับ input จากผู้ใช้
   - ประมวลผลคำสั่ง
   - แสดงผลการทำรายการ

### 4. **เมนูหลัก (Main Menu)**
   - ดูรายการสินค้า
   - สร้างใบเสร็จใหม่
   - ดูประวัติการขาย
   - ออกจากโปรแกรม

## 🏗️ สถาปัตยกรรม OOP

โปรเจคนี้ประยุกต์ใช้หลักการ OOP ต่อไปนี้:

- **Encapsulation** - ซ่อน data ที่ไม่จำเป็น ใช้ getter/setter
- **Inheritance** - สืบทอด class เพื่อนำกลับมาใช้อีก
- **Polymorphism** - override method สำหรับพฤติกรรมที่ต่างกัน
- **Abstraction** - สร้าง abstract class หรือ interface เมื่อจำเป็น

## 📊 ตัวอย่างการใช้งาน

```
========== เมนูหลัก ==========
1. ดูรายการสินค้า
2. สร้างใบเสร็จใหม่
3. ดูประวัติการขาย
4. ออกจากโปรแกรม
============================

กรุณาเลือกตัวเลือก (1-4): 1

========== รายการสินค้า ==========
[001] ข้าวหน้าหมู         120 บาท
[002] ข้าวหน้าไก่         110 บาท
[003] ชาเขียว            30 บาท
[004] แคปูชิโน่          60 บาท
================================
```

## 👨‍💻 ผู้พัฒนา
- [MONDAE333](https://github.com/MONDAE333)

## 📅 ข้อมูลโปรเจค
- **สร้าง:** 15 มีนาคม 2026
- **แก้ไขครั้งล่าสุด:** 23 เมษายน 2026
- **ประเทศ:** ประเทศไทย (Moshi Moshi)

## 📚 เนื้อหาที่เรียนรู้

ในโปรเจคนี้ คุณจะได้เรียนรู้เกี่ยวกับ:

- การออกแบบ Class Structure
- การจัดการ Object และ Instance
- การใช้ Constructor และ Initialization
- Getter/Setter Methods
- Class Inheritance และ super keyword
- Method Overriding
- Collection (ArrayList, HashMap) สำหรับจัดเก็บข้อมูล
- Exception Handling เบื้องต้น

## 🎓 หมายเหตุ

โปรเจคนี้เป็นส่วนหนึ่งของการเรียนรู้ OOP ระดับพื้นฐาน 
ถ้าต้องการพัฒนาต่อ สามารถเพิ่มฟีเจอร์ต่อไปนี้ได้:

- 💾 ระบบเก็บข้อมูลลงไฟล์ (File I/O)
- 🔐 ระบบเข้าสู่ระบบ (Authentication)
- 📊 ระบบรายงาน (Report Generation)
- 🗄️ ฐานข้อมูล (Database Integration)

## 📧 ติดต่อ

หากมีคำถามหรือข้อเสนอแนะ สามารถติดต่อผ่าน:
- GitHub Issues: [Issues Page](https://github.com/MONDAE333/Psu_Miniproject_OOP/issues)
- GitHub Profile: [MONDAE333](https://github.com/MONDAE333)

---

**Happy Coding! 🚀**
