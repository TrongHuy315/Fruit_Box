import time
import random
from datetime import datetime, timezone
import numpy as np
from tqdm import tqdm

# Thông tin từ đề bài
VALUE_LIST_STR = [123790633, 156591608, 147000916, 125772724, 131152757, 152644709, 144001980, 112930002, 118794552, 150363810, 132253260, 129427768, 125337368, 132414473, 139338226, 121261563, 134915261, 133063748, 129569576, 135580576, 141567869, 142129037, 150793830, 139504515, 143094641, 143348690, 133597992]
VALUE_LIST = np.array(VALUE_LIST_STR, dtype=np.float64)

FLAG_PREFIX_STR = "0160ca14{https://"
FLAG_SUFFIX_STR = "}"
FLAG_LEN = 46
NUM_EQUATIONS = 27

FLAG_PREFIX_VAL = list(FLAG_PREFIX_STR.encode('ascii'))
FLAG_SUFFIX_VAL = list(FLAG_SUFFIX_STR.encode('ascii'))[0]

# Chỉ số các phần của flag
PREFIX_LEN = len(FLAG_PREFIX_VAL) # 17
# Ký tự thứ 18 (index 17) là ký tự ta bruteforce
IDX_BRUTEFORCE_CHAR = PREFIX_LEN # 17
# Khối ẩn số bắt đầu từ index 18
START_IDX_UNKNOWN_BLOCK = IDX_BRUTEFORCE_CHAR + 1 # 18
# Khối ẩn số kết thúc ở index 44 (FLAG_LEN - 2)
END_IDX_UNKNOWN_BLOCK = FLAG_LEN - 2 # 44
# Số lượng ẩn số trong khối
NUM_UNKNOWN_IN_BLOCK = END_IDX_UNKNOWN_BLOCK - START_IDX_UNKNOWN_BLOCK + 1 # 27
# Index của ký tự cuối cùng
IDX_SUFFIX_CHAR = FLAG_LEN - 1 # 45


def generate_coefficients_matrix(seed_val):
    random.seed(seed_val)
    # Ma trận coefficients_list (M) sẽ có kích thước NUM_EQUATIONS x FLAG_LEN (27x46)
    matrix = []
    for _ in range(NUM_EQUATIONS):
        row = [random.randint(1, 2**16) for _ in range(FLAG_LEN)]
        matrix.append(row)
    return np.array(matrix, dtype=np.float64)

# Thời gian dự kiến script được chạy (UTC)
# "public vào lúc 11h27AM 15/5/2025"
# "kiểm thử vài phút trước khi đăng lên"
# Giả sử script chạy trong khoảng 11:00 AM đến 11:27:59 AM
# Luôn sử dụng timezone UTC khi làm việc với timestamp từ time.time()
# Djt me bịp vãi loz, nó dùng giờ GMT +7, mà mình lại dùng giờ UTC
# Tức là 11AM là 4AM
start_datetime = datetime(2025, 5, 15, 4, 0, 0, tzinfo=timezone.utc)
end_datetime = datetime(2025, 5, 15, 4, 27, 59, tzinfo=timezone.utc) # Bao gồm cả giây cuối của phút 27

start_seed = int(start_datetime.timestamp())
end_seed = int(end_datetime.timestamp())

print(f"[*] Searching seeds from {start_seed} ({start_datetime}) to {end_seed} ({end_datetime})")
print(f"[*] Flag structure: {FLAG_PREFIX_STR}?[{NUM_UNKNOWN_IN_BLOCK} chars]{FLAG_SUFFIX_STR}")

found_flag_solution = False
for current_seed in tqdm(range(start_seed, end_seed + 1), desc="Scanning Seeds"):
    # 1. Tạo ma trận coefficients (M) từ seed
    # M có kích thước 27x46
    M = generate_coefficients_matrix(current_seed)

    # 2. Tính toán đóng góp của các phần đã biết (prefix và suffix) vào vế phải
    # M_prefix là các cột 0-16 của M. M_prefix @ FLAG_PREFIX_VAL
    prefix_contribution = np.dot(M[:, :PREFIX_LEN], FLAG_PREFIX_VAL)
    
    # M_suffix_col là cột 45 của M.
    suffix_contribution = M[:, IDX_SUFFIX_CHAR] * FLAG_SUFFIX_VAL

    # 3. Bruteforce ký tự thứ 18 (index 17)
    # Giá trị ASCII từ 32 (space) đến 126 (~)
    for char18_ascii_val in range(32, 127):
        # Đóng góp của ký tự thứ 18
        # M_char18_col là cột 17 của M
        char18_contribution = M[:, IDX_BRUTEFORCE_CHAR] * char18_ascii_val

        # 4. Tính toán vế phải mới (target_rhs) cho hệ phương trình của khối ẩn
        # target_rhs = VALUE_LIST - (prefix_contrib + char18_contrib + suffix_contrib)
        target_rhs = VALUE_LIST - prefix_contribution - char18_contribution - suffix_contribution

        # 5. Lấy ma trận con M_sub cho khối 27 ký tự ẩn (cột 18 đến 44)
        # M_sub có kích thước 27x27
        M_sub = M[:, START_IDX_UNKNOWN_BLOCK : END_IDX_UNKNOWN_BLOCK + 1]
        
        if M_sub.shape[1] != NUM_UNKNOWN_IN_BLOCK: # Sanity check
            # print(f"Skipping seed {current_seed}, char {char18_ascii_val}: M_sub shape {M_sub.shape} incorrect")
            continue

        try:
            # 6. Giải hệ M_sub * F_unknown = target_rhs
            solved_unknown_block = np.linalg.solve(M_sub, target_rhs)

            # 7. Kiểm tra nghiệm
            rounded_unknown_block = np.round(solved_unknown_block)
            
            # Kiểm tra xem nghiệm có gần số nguyên không
            if not np.allclose(solved_unknown_block, rounded_unknown_block, atol=1e-9):
                continue

            # Kiểm tra xem các giá trị có nằm trong khoảng ASCII hợp lệ không
            # (32 đến 126 là các ký tự in được)
            if not np.all((rounded_unknown_block >= 32) & (rounded_unknown_block <= 126)):
                continue
            
            # Nếu mọi thứ ổn, đã tìm thấy flag!
            reconstructed_flag_values = []
            reconstructed_flag_values.extend(FLAG_PREFIX_VAL)
            reconstructed_flag_values.append(char18_ascii_val)
            reconstructed_flag_values.extend(rounded_unknown_block.astype(int).tolist())
            reconstructed_flag_values.append(FLAG_SUFFIX_VAL)
            
            final_flag_bytes = bytes(reconstructed_flag_values)
            try:
                final_flag_str = final_flag_bytes.decode('ascii')
                
                # Xác minh lại lần cuối (quan trọng!)
                # Tính lại value_list từ flag tìm được và M
                recalculated_values = np.dot(M, reconstructed_flag_values)
                if np.allclose(recalculated_values, VALUE_LIST, atol=1): # Cho phép sai số nhỏ do làm tròn

                    print(f"\n[+] SUCCESS! Flag found with seed: {current_seed}")
                    print(f"    Timestamp: {datetime.fromtimestamp(current_seed, tz=timezone.utc)}")
                    print(f"    18th char (ASCII {char18_ascii_val}): '{chr(char18_ascii_val)}'")
                    print(f"    Flag: {final_flag_str}")
                    found_flag_solution = True
                    break # Thoát vòng lặp bruteforce ký tự
                else:
                    # print(f"Verification failed for seed {current_seed}, char {chr(char18_ascii_val)}")
                    pass

            except UnicodeDecodeError:
                # print(f"UnicodeDecodeError for seed {current_seed}, char {chr(char18_ascii_val)}")
                pass # Không phải flag hợp lệ

        except np.linalg.LinAlgError:
            # Ma trận M_sub không khả nghịch (singular matrix)
            pass
    
    if found_flag_solution:
        break # Thoát vòng lặp seed

if not found_flag_solution:
    print("\n[-] No flag found with the current parameters and seed range.")
